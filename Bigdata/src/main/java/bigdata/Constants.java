package bigdata;
public interface Constants {

    /**
     * spark相关
     */
    String SPARK_WAREHOUSE = "spark.warehouse";

    /**
     * hive相关
     */
    String HIVE_VERSION = "hive.version";

    /**
     * mysql 主库相关
     */
    String MYSQL_USER_BIG_TEST = "mysql.user.big.test";
    String MYSQL_PASSWD_BIG_TEST = "mysql.password.big.test";
    String MYSQL_URL_BIG_TEST = "mysql.url.big.test";
    String MYSQL_DB_BIG_TEST = "mysql.db.big.test";

    /**
     * mysql 分库相关
     */
    String MYSQL_USER_SPLIT_TEST = "mysql.user.split.test";
    String MYSQL_PASSWD_SPLIT_TEST = "mysql.password.split.test";
    String MYSQL_URL_SPLIT_TEST = "mysql.url.split.test";
    String MYSQL_DB_SPLIT_TEST = "mysql.db.split.test";

    /**
     * MySQL risk备库
     */
    String MYSQL_URL_RISK = "mysql.url.risk";
    String MYSQL_DB_RISK = "mysql.db.risk";

    //risk0_9
    String MYSQL_URL_RISK_SPLIT = "mysql.url.risk.split";
    String MYSQL_DB_RISK_SPLIT = "mysql.db.risk.split";

    /**
     * mysql表导入hdfs参数
     */
    String TABLE_HDFS_PATH = "table.hdfs.path";
    String TABLE_HDFS_TABLE = "table.hdfs.name";
    String TABLE_IMPORT_OFFSET = "table.import.offset";
    String TABLE_IMPORT_START = "table.import.start";
    String TABLE_IMPORT_ALLTABLES = "table.import.alltables";

    /**
     * zk相关
     */
    String PARAM_ZOOKEEPER_LIST = "param.zookeeper.list";
    String PARAM_ZOOKEEPER_PORT_LIST= "param.zookeeper.port.list";

    /**
     * 日期相关
     */
    String CALCU_STARTTIME = "calcu.starttime";
    String CALCU_ENDTIME = "calcu.endtime";

    /**
     * 计算变量
     */
    String PARAM_CALCU_NDAYBEFORE = "param.calcu.ndaybefor";

    /**
     * kafka相关
     */
    String  METADATA_BROKER_LIST = "metadata.broker.list";
    String KAFKA_GROUP_ID = "kafka.group.id";
    String KAFKA_TOPIC_NAME = "kafka.topic.name";


    String DATA_HDFS_PATH="data.hdfs.path";
    String ALL_TABLE="table.all";
    String SPARK_MONGODB_OUTPUT_URL="spark.mongodb.output.uri";
    String SPARK_MONGODB_INPUT_URL="spark.mongodb.input.uri";
    String MYSQL_DRVER="com.mysql.jdbc.Driver";
    //String MYSQL_URL="jdbc:mysql://rr-uf6hfu63h0yv7lon9.mysql.rds.aliyuncs.com:3306/soros_risk";
    String MYSQL_URL ="mysql.url";
    //String MYSQL_USERNAME="bigdata";
    String MYSQL_USERNAME="root";
    //String MYSQL_PASSWORD="Big_YWRmOData!";
    String MYSQL_PASSWORD="123456";
    String HBASE_TABLE="hbase.table";
    String HBASE_COL="hbase.col";
    String ZK_LIST="zk.list";


    String DATE="date_pro";


}
