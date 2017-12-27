package Bigdata

import java.io.{PrintWriter, FileSystem => _}
import java.sql.DriverManager
import java.text.SimpleDateFormat
import java.util.Date

import bigdata.{ConfigurationManagers, Constants, HbaseDo}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import scala.util.control.Breaks.{break, breakable}
object NewMong {



  //mysql配置设置
  val driver = "com.mysql.jdbc.Driver"

  //hdfs配置
  val conf = new Configuration()
  conf.set("fs.defaultFS", "hdfs://bigdata1:8020")
  conf.setBoolean("dfs.support.append", true)
  val fs= FileSystem.get(conf)



  def MoveMysqlToHdfs:Unit={

    //mysql配置信息
    Class.forName(driver)
    println("111111111111")
    val connection = DriverManager.getConnection(ConfigurationManagers.getProperty(Constants.MYSQL_URL), ConfigurationManagers.getProperty(Constants.MYSQL_USERNAME), ConfigurationManagers.getProperty(Constants.MYSQL_PASSWORD))
    val dbName="soros_risk"
    val tableName="risk_mobile_info"

    //hbase配置信息
    val HbaseTable=ConfigurationManagers.getProperty(Constants.HBASE_TABLE)
    val col=ConfigurationManagers.getProperty(Constants.HBASE_COL)

    //获取当天时间
    val format =new SimpleDateFormat("yyyy-MM-dd")
    val date= format.format(new Date().getTime)
    val TodaysStamp = format.parse(date).getTime
    val Today=format.format(TodaysStamp)

    //获取前一天时间
    val dateset:Long=TodaysStamp - 24*60*60*1000
    val Yestoday=format.format(dateset)

    //每次写入10000条数据
    var startid:Long=0
    var endid:Long =startid+10000

    //上一次的偏移量
    val rowKey =dbName + "_" + tableName + "_" + Yestoday  //hbase的行健
    var last_off_set = HbaseDo.getRow(HbaseTable, rowKey, col)

    if(last_off_set == null || last_off_set.equals("")){

      println("第一次插入数据为空")
      last_off_set = "0"
      startid=last_off_set.toLong
    }

    breakable {
      while (true) {

        val k = "select * from "+tableName +" where id > " + startid + " and id <= " + endid
        println("开始的start id："+startid)
        val statement = connection.prepareStatement(k)
        var resultSet = statement.executeQuery()
        var md = resultSet.getMetaData
        var coulment = md.getColumnCount

        var output = fs.create(new Path(ConfigurationManagers.getProperty(Constants.DATA_HDFS_PATH)+ startid))
        var out =new PrintWriter(output)
        //遍历mysql表中的数据，并作追加分隔符处理
        while (resultSet.next()) {

          var row = ""
          for (i <- 1 to coulment) {

            var col = resultSet.getString(i)
            row = row + col + "\001"

          }
          out.write(row)
          out.write("\r\n")
        }
        out.close()
        output.close()

        //如果存在startid不从1开始的，从大于50000的开始判断
        if (startid>50000) {
          resultSet.last()
          val rowCount = resultSet.getRow()
          if (rowCount == 0) {

           println("表" + tableName + "数据已导完")
            break
          }
        }

        //记录偏移量到hbase
        var finrowkey =dbName + "_" + tableName + "_" + Today
        HbaseDo.insertData(HbaseTable, finrowkey, col, "startid", startid+"")
        HbaseDo.insertData(HbaseTable, finrowkey, col, "endid", endid+"")
        startid += 10000
        endid += 10000
        statement.close()
        resultSet.close()
      }
    }
  }

  def main(args: Array[String]): Unit = {

    //数据写如hdfs
    MoveMysqlToHdfs

  }


}
