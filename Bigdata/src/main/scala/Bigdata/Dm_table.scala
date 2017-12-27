package Bigdata

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import bigdata.{ConfigurationManagers, Constants, HbaseDo}

object Dm_table {

   //获取当天时间
  val format_year =new SimpleDateFormat("yyyy")
  val format_month =new SimpleDateFormat("MM")
  val format_day =new SimpleDateFormat("dd")
  val format_year_month_day =new SimpleDateFormat("yyyy-MM-dd")
  val format_time =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  var date= ""
  var year=""
  var month=""
  var day=""
  var time =""
  val tablename ="datemember"

  val date_properties=ConfigurationManagers.getProperty(Constants.DATE)

  def main(args: Array[String]): Unit = {


    //如果配置文件中有配置时间，按配置文件配置的时间，否则获取当天的时间
    if(date_properties==""){

      date=format_year_month_day.format(new Date().getTime)
      year=format_year.format(new Date().getTime)
       month=format_month.format(new Date().getTime)
       day=format_day.format(new Date().getTime)
       time =format_time.format(new Date().getTime)
    }else{
      date=date_properties.split(" ")(0)
      year=date_properties.split("-")(0)
      month=date_properties.split("-")(1)
      day=date_properties.split(" ")(0).split("-")(2)
      time=date_properties
    }

    //日期ID
    val day_dm=date.replaceAll("-","")
    val dd =date.split("-")(0)
    //rowkey
    val rowkey=day_dm
    //日期名称
    val day_name=year+"年"+month+"月"+day+"日"
    //周ID
    var week_dm=""
    //周名称
    var week_name =""
    //月ID
    val month_dm =year+month
    //月名称22
    val month_name =year+"年"+month+"月"
    //周年ID
    var year_week_dm=0

    println("month="+month+"---"+"week="+week)
    if(month=="12" && week==1){

       year_week_dm =year.toInt+1
      week_dm=year_week_dm+"_"+week
      week_name=year_week_dm+"年第"+week+"周"

    }else if(month=="1"&&week_dm.toInt>1){
       year_week_dm=year.toInt-1
      week_dm=year_week_dm+"_"+week
      week_name=year_week_dm+"年第"+week+"周"
    }else{

      year_week_dm=year.toInt
      week_dm=year+"_"+week
      week_name=year+"年第"+week+"周"
    }
    //周年名称
    val year_week_name =year_week_dm+"年"
    //月年ID
    val year_month_dm =year
    //月年名称
    val year_month_name =year+"年"
    //创建人
    val createby ="herry.sun"
    //创建时间
    val createtime =time
    //修改时间
    val changetime ="0"
    //结束时间
    val endtime ="0"


    //插入hbase
    HbaseDo.insertData(tablename,rowkey,"cf1","day_dm",day_dm)
    HbaseDo.insertData(tablename,rowkey,"cf1","day_name",day_name)
    HbaseDo.insertData(tablename,rowkey,"cf1","week_dm",week_dm.toString)
    HbaseDo.insertData(tablename,rowkey,"cf1","week_name",week_name)
    HbaseDo.insertData(tablename,rowkey,"cf1","month_dm",month_dm)
    HbaseDo.insertData(tablename,rowkey,"cf1","month_name",month_name)
    HbaseDo.insertData(tablename,rowkey,"cf1","year_week_dm",year_week_dm.toString)
    HbaseDo.insertData(tablename,rowkey,"cf1","year_week_name",year_week_name)
    HbaseDo.insertData(tablename,rowkey,"cf1","year_month_dm",year_month_dm)
    HbaseDo.insertData(tablename,rowkey,"cf1","year_month_name",year_month_name)
    HbaseDo.insertData(tablename,rowkey,"cf1","createby",createby)
    HbaseDo.insertData(tablename,rowkey,"cf1","createtime",createtime)
    HbaseDo.insertData(tablename,rowkey,"cf1","changetime",changetime)
    HbaseDo.insertData(tablename,rowkey,"cf1","endtime",endtime)

  }


  /**
    * 获取日期当中所对应的"周"
    */
  def week:Int={
    var week=1
    val format_year_month_day =new SimpleDateFormat("yyyy-MM-dd")
    if(date_properties==""){

      val date= format_year_month_day.parse(format_year_month_day.format(System.currentTimeMillis()))
      val calendar = Calendar.getInstance
      calendar.setFirstDayOfWeek(Calendar.MONDAY)
      calendar.setTime(date)
      week=calendar.get(Calendar.WEEK_OF_YEAR)
    }else{

      val date= format_year_month_day.parse(date_properties.split(" ")(0))
      val calendar = Calendar.getInstance
      calendar.setFirstDayOfWeek(Calendar.MONDAY)
      calendar.setTime(date)
      week=calendar.get(Calendar.WEEK_OF_YEAR)
    }

    week
  }

}
