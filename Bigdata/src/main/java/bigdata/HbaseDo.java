package bigdata;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.mapred.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HbaseDo {


    /**
     * 批量插入数据
     * @param tablename     表名
     * @param puts           需要添加的数据
     * @throws IOException
     */
    public static void putByHTable(String tablename, List<?> puts) throws Exception {
        Configuration  conf = new Configuration();
        String zk_list=ConfigurationManagers.getProperty(Constants.ZK_LIST);
        //String zk_list = ConfigurationManagers.getProperty(Constants.PARAM_ZOOKEEPER_LIST);
        conf.set("hbase.zookeeper.quorum", zk_list);
        conf.set("hbase.zookeeper.property.clientPort", "2181");

        Connection connection = ConnectionFactory.createConnection(conf);
        HTable htable = (HTable) connection.getTable(TableName.valueOf(tablename));
        //htable.setAutoFlushTo(true);
        //htable.setWriteBufferSize(5 * 1024 * 1024);
        try {
            htable.put((List<Put>) puts);
            htable.flushCommits();
        } finally {
            htable.close();
            connection.close();
        }
    }

    /**
     * 插入单条数据到hbase
     * @param tableName
     * @param rowKey
     * @param quailifer
     * @param value
     */
    public static void insertData(String tableName, String rowKey,String family,
                                  String quailifer, String value) {

        Configuration  conf = new Configuration();
        //String zk_list = "master,slaves1,slaves2";
        String zk_list=ConfigurationManagers.getProperty(Constants.ZK_LIST);
        conf.set("hbase.zookeeper.quorum", zk_list);
        conf.set(TableOutputFormat.OUTPUT_TABLE, tableName);
        Connection conn = null;
        HTable hTable = null;

        try {
            conn = ConnectionFactory.createConnection(conf);
            hTable = (HTable) conn.getTable(TableName.valueOf(tableName));
            hTable.setAutoFlushTo(true);
            Put put = new Put(rowKey.getBytes());
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(quailifer), Bytes.toBytes(value));
            //put.setWriteToWAL(false);
            hTable.put(put);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != hTable) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据rowKey查询
     * @param tableName 表名
     * @param row 行健
     * @param column 列名
     * @return  值
     */
    public static String getRow(String tableName, String row,String column)  {
        Table table = null;
        Connection conn = null;
        Configuration  conf = new Configuration();
        String zk_list=ConfigurationManagers.getProperty(Constants.ZK_LIST);
        //String zk_list="bigdata6,bigdata7,bigdata8,bigdata9,bigdata10";
        conf.set("hbase.zookeeper.quorum", zk_list);
        // 取得数据表对象
        String v = "";

        try {

            conn = ConnectionFactory.createConnection(conf);
            table = conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(row));


            get.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("startid"));
            get.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("endid"));


            Result result = table.get(get);
            List<KeyValue> list = result.list();
            if(list != null){
                for(KeyValue k: list){
                    v = (Bytes.toString(k.getValue()));
                    break;
                }
            }else{
                System.out.println("^^^^^^^^^^^^^^^^^^没有数据^^^^^^^^^^^^^^^^^^");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != table) {
                try {
                    table.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("^^^^^^^^^^^^^^^^^^Getrow第4步^^^^^^^^^^^^^^^^^^^");
            if (null != conn) {
                try {
                    System.out.println("^^^^^^^^^^^^^^^^^^Getrow第5步^^^^^^^^^^^^^^^^^^^");
                    conn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return v;
    }


    /**
     * 创建表
     */
    public static void createTable(String tableName) {
        System.out.println("start create table ......");

        Configuration  conf = new Configuration();
        // String zk_list = "master,slaves1,slaves2";
        String zk_list=ConfigurationManagers.getProperty(Constants.ZK_LIST);
        conf.set("hbase.zookeeper.quorum", zk_list);
        try {
            HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);
            if (hBaseAdmin.tableExists(tableName)) {// 如果存在要创建的表，那么先删除，再创建
                hBaseAdmin.disableTable(tableName);
                hBaseAdmin.deleteTable(tableName);
                System.out.println(tableName + " is exist,detele....");
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
            tableDescriptor.addFamily(new HColumnDescriptor("cf1"));
            hBaseAdmin.createTable(tableDescriptor);
        } catch (MasterNotRunningException e) {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("end create table ......");
    }

    /**
     * 过滤查询最大的rowKey
     *
     * @param tableName 表名
     * @param rowLike rowkey的开头
     * @throws IOException
     */
    public static String getMaxRowkey(String tableName,String rowLike) throws IOException {
        List<String> list = null;
        Configuration  conf = new Configuration();
        String zk_list = ConfigurationManagers.getProperty(Constants.PARAM_ZOOKEEPER_LIST);
        conf.set("hbase.zookeeper.quorum", zk_list);
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        Connection connection = ConnectionFactory.createConnection(conf);
        HTable table = (HTable) connection.getTable(TableName.valueOf(tableName));

        try {
            list = new ArrayList<String>();
            Scan scan = new Scan();
            Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator(rowLike+".*"));
            scan.setFilter(filter);
            scan.setReversed(true);
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                String row = new String(result.getRow(), "UTF-8");
                list.add(row);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(list);
        if(list.size() == 0){
            return null;
        }else {
            String maxRoeKey = list.get(list.size() - 1);
            return maxRoeKey;
        }

    }


}
