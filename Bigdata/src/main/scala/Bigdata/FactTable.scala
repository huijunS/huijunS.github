package DataPlatform

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import DataPlatform.Dm_table.tablename
import DataPlatform.FactTable.time
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{Row, SparkSession}
import shj.{ConfigurationManagers, Constants, HbaseDo, UDF_PinYin}

object FactTable {

  val tablename ="fact_order_loan"
  val sparkSession= SparkSession.builder().master("local").
    config("spark.sql.hive.metastore.version", ConfigurationManagers.getProperty(Constants.HIVE_VERSION)).
    config("spark.sql.warehouse.dir",ConfigurationManagers.getProperty(Constants.SPARK_WAREHOUSE)).
    enableHiveSupport.
    getOrCreate()

  //获取日期
  val format = new SimpleDateFormat("yyyy-MM-dd")
  val date = format.format(new Date().getTime).replaceAll("-", "") + ""
  var time = ""


  val date_properties=ConfigurationManagers.getProperty(Constants.DATE)
  if(date_properties==""){
    //四个月前的日期
    time=(Integer.parseInt(date)-9200).toString
  }else{
    time= date_properties.split(" ")(0).replaceAll("-", "") + ""

  }


  //spark udf方法
  val udf =new UDF_PinYin
  sparkSession.udf.register("pinyin",(ChineseLanguage:String) =>udf.evaluate(ChineseLanguage))

  def main(args: Array[String]): Unit = {

    val data =sparkSession.sql("select  " +
      "b.order_no as order_no, " +
      "regexp_replace(to_date(a.notify_time),'-','') as day_dim," +
      "regexp_replace(to_date(a.notify_time),'-','') as part_day," +
      "case when d.productname!='' and d.productname is not null then pinyin(d.productname) end as product_dim , " +
      "case " +
      "when e.rownum =1 and e.success_status='01' then 'xinyonghu'  " +
      "when  e.rownum > 1 and e.success_status='01' then 'laoyonghu'  end as user_dim ," +
      "case when b.period =7 and a.status='02' then '1' end as order_7 ," +
      "case when b.period =14 and a.status='02' then '1'  end as order_14 ," +
      "case when b.period =30 and a.status='02' then '1' end as order_30 ,  " +
      "case when b.period = 7 and a.status='02' then b.loan_amount end as loan_7 ," +
      "case when b.period = 14 and a.status='02' then b.loan_amount end as loan_14 ," +
      "case when b.period = 30 and a.status='02' then b.loan_amount end as loan_30 " +
      "from " +
      "soros_pay.pay_instant_pay a " +
      "left outer join " +
      "soros_order.order_orders b on (a.buss_order_no = b.order_no) " +
      "left outer join " +
      "soros_credit.credit_product d on (b.product_no =d.productno) " +
      "left outer join " +
      "soros_order.order_from e on (b.order_no =e.order_no) " +
      "where regexp_replace(to_date(a.notify_time),'-','') = " + "'"+time+"'"
    )
    data.show(50)
    val tbPuts = new util.ArrayList[Put]
    val coloums=data.columns

    data.foreach(x=>{

      for(i<- 0 to coloums.size-1){
        val id=x.getString(0)
        val col = x.getAs[String](coloums(i))
        val value = if (col == null) ""
        else col

        //批量插入hbase
        tbPuts.add(new Put(id.toString.getBytes).addColumn(Bytes.toBytes("cf1"), Bytes.toBytes(coloums(i)), Bytes.toBytes(value.toString)))
      }

      HbaseDo.putByHTable(tablename, tbPuts)

    })
  }
}
