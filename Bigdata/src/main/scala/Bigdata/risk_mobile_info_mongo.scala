package Bigdata

import bigdata.{ConfigurationManagers, Constants}
import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.SparkSession

object risk_mobile_info_mongo {


  val sparkSession= SparkSession.builder().
    config("spark.mongodb.output.uri", "mongodb://192.168.1.113:27017/local.hello1").
    //config("spark.mongodb.output.uri", ConfigurationManagers.getProperty(Constants.SPARK_MONGODB_OUTPUT_URL)+"local.risk_contacts").
    config("spark.sql.hive.metastore.version", ConfigurationManagers.getProperty(Constants.HIVE_VERSION)).
    config("spark.sql.warehouse.dir",ConfigurationManagers.getProperty(Constants.SPARK_WAREHOUSE)).
    enableHiveSupport.
    getOrCreate()


  def main(args: Array[String]): Unit = {

     sparkSession.sql("use test")

    /*获取risk_mobile_info表的数据
    * seq_no，member_no，contacts，contacts_cnt，ip，device_id,device_info,sim_id,sim_phone,phone_mac,
    * wifi_mac,gravity,login_type,remark,create_operator,last_update_operator,md5,create_time,last_update_time,as _class
    * 并替换字段
    * */

    val k=sparkSession.sql("select " +
      "seq_no," +
      "member_no," +
      "contacts," +
      "contacts_cnt," +
      "ip," +
      "device_id," +
      "device_info," +
      "sim_id," +
      "sim_phone," +
      "phone_mac," +
      "wifi_mac," +
      "gravity," +
      "login_type," +
      "remark," +
      "create_operator," +
      "last_update_operator," +
      "md5, " +
      "create_time," +
      "last_update_time," +
      "'com.soros.facade.risk.model.RiskMobileInfo' as _class " +
      "from test.two  where id < 8883555 ")
      .withColumnRenamed("seq_no","seqNo").withColumnRenamed("member_no","memberNo").withColumnRenamed("contacts_cnt","contactsCnt").
        withColumnRenamed("device_id","deviceId").withColumnRenamed("device_info","deviceInfo").withColumnRenamed("sim_id","simId").
    withColumnRenamed("sim_phone","simPhone").withColumnRenamed("phone_mac","phoneMac").withColumnRenamed("wifi_mac","wifiMac").
    withColumnRenamed("login_type","loginType").withColumnRenamed("create_operator","createOperator").withColumnRenamed("create_time","createTime").
    withColumnRenamed("last_update_operator","lastUpdateOperator").withColumnRenamed("last_update_time","lastUpdateTime")


    //数据写入mongogb
    MongoSpark.save(k)

  }
}
