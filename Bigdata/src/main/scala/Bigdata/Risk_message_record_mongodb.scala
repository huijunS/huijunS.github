package Bigdata

import bigdata.{ConfigurationManagers, Constants}
import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.SparkSession
object Risk_message_record_mongodb {


  val sparkSession= SparkSession.builder().
    config("spark.mongodb.output.uri", ConfigurationManagers.getProperty(Constants.SPARK_MONGODB_INPUT_URL)+"soros_risk.risk_message_record").
    config("spark.sql.hive.metastore.version", ConfigurationManagers.getProperty(Constants.HIVE_VERSION)).
    config("spark.sql.warehouse.dir",ConfigurationManagers.getProperty(Constants.SPARK_WAREHOUSE)).
    enableHiveSupport.
    getOrCreate()

  def main(args: Array[String]): Unit = {

    //获取hive表数据 并注册成临时表
    val result_message= sparkSession.sql("select " +
      "a.*," +
      "b.ecif_no " +
      "from test.risk_message_record a " +
      "join test.risk_carrier b " +
      "on a.carrier_id=b.id "
      ).createOrReplaceTempView("risk_message_record_mongo")


     /*获取risk_message_record的数据
      * carrier_id，send_time，message_type，caller_no，fee，create_time，_class，ecif_no
      * 并替换字段
      * */

    sparkSession.sql("use test")
    val result =sparkSession.sql("select " +
      "carrier_id, " +
      "send_time, " +
      "message_type, " +
      "caller_no, " +
      "fee, " +
      "create_time , " +
      "'com.soros.facade.risk.model.RiskMessageRecord' as _class, " +
      "ecif_no  " +
      "from risk_message_record_mongo ").
      withColumnRenamed("carrier_id","carrierId").withColumnRenamed("send_time","sendTime").withColumnRenamed("message_type","messageType").
      withColumnRenamed("create_time","createTime").withColumnRenamed("ecif_no","ecifNo").withColumnRenamed("caller_no","callerNo")



    //数据写入mongodb
    MongoSpark.save(result)
  }
}
