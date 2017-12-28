package Bigdata

import java.io.PrintWriter
import java.util

import bigdata.{ConfigurationManagers, Constants}
import com.mongodb.spark.MongoSpark
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession

object MongodbToHive {

  val conf = new Configuration()
  conf.setBoolean("dfs.support.append", true)
  val fs = FileSystem.get(conf)
  val output = fs.create(new Path(ConfigurationManagers.getProperty(Constants.DATA_HDFS_PATH)))
  val out = new PrintWriter(output)



  /*hive 中有的字段mongodb中不一定有
  *hive中的字段名为seq_no，member_no。但是mongodb中的字段名维seqNo,memberNo 需要转换
  * */
  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder().master("local").
      config("spark.mongodb.input.uri", "mongodb://192.168.1.113:27017/local.helloyes").
      config("spark.sql.hive.metastore.version", ConfigurationManagers.getProperty(Constants.HIVE_VERSION)).
      config("spark.sql.warehouse.dir", ConfigurationManagers.getProperty(Constants.SPARK_WAREHOUSE)).
      enableHiveSupport.
      getOrCreate()

    //hive字段名
    val hdfs_hive = sparkSession.sql("select * from test.four").schema.fieldNames
    //mongodb的数据
    var mongo_data = MongoSpark.load(sparkSession)
    //mongodb字段名
    val mongo_type = mongo_data.schema.fieldNames
    val mongoHiveFieldsMap = new util.HashMap[String, String]

    for (i <- 0 to mongo_type.size - 1) {
      //mongodb里面的字段名
      val mongofield = mongo_type(i)
      var hiveField = mongofield
      //找出大写的字母
      val upCases = getUpCase(mongofield)
      for (j <- 0 to upCases.size()-1) {

        val upCase = upCases.get(j)
        hiveField = hiveField.replace(upCase, "_" + upCase.toLowerCase())
      }
      mongoHiveFieldsMap.put(mongofield, hiveField)
    }

    //遍历map更改mongo的dataset列名
    val entries = mongoHiveFieldsMap.entrySet()
    val iterator = entries.iterator
    while (iterator.hasNext) {
      val entry = iterator.next
      val mongoFiledName = entry.getKey
      val hiveFiledName = entry.getValue
      mongo_data = mongo_data.withColumnRenamed(mongoFiledName, hiveFiledName)

    }

    //mongodb中的字段名转成list
    val mongoFiledsList = mongo_data.schema.fieldNames.toList

    //遍历hive字段名
    mongo_data.foreach(x=>{

      val hiveFieldNList=hdfs_hive.toList
      var fieldIndexMap = new util.HashMap[String, String]
      var modelStr = ""
      var hiveFiled=""

      for(k<- 0 to hiveFieldNList.size-1){
        hiveFiled = hiveFieldNList(k)
        if (mongoFiledsList.contains(hiveFiled)) {
          fieldIndexMap.put(hiveFiled,x.getAs(hiveFiled) )

          modelStr = modelStr+fieldIndexMap.get(hiveFiled) + "\001"
        }else {
          modelStr=modelStr+"null"+"\001"
        }
        //println(hiveFiled)
        //如果mongo对应的字段里面有hive的，记录下mongo字段在hive的位置,并在位置打下当前字段的位置值
      }

      out.write(modelStr)
      out.write("\r\n")

    })
    out.close()
    output.close()
  }


  /**
    * 返回字符串中的大写字母为list
    */
  def getUpCase(str: String): util.ArrayList[String] = {

    val list: util.ArrayList[String] = new util.ArrayList[String]()
    val buffer: StringBuffer = new StringBuffer()
    val ch: Array[Char] = str.toCharArray
    for (i <- 0 to ch.length - 1) {
      if (ch(i) >= 'A' && ch(i) <= 'Z') {
        list.add(ch(i)+"")
      }
    }
    list
  }
}