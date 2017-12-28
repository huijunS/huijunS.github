package PhoenixTest

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row

object SparkTest {


  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("Spark SQL basic example").config("spark.some.config.option", "some-value").master("local").getOrCreate()
    import spark.implicits._

    val dataList: List[(Double, String, Int, Double, String, Double, Double, Double, Double)] = List(
      (0, "male", 37, 10, "no", 3, 18, 7, 4),
      (0, "female", 27, 4, "no", 4, 14, 6, 4),
      (0, "female", 32, 15, "yes", 1, 12, 1, 4),
      (0, "male", 57, 15, "yes", 5, 18, 6, 5),
      (0, "male", 22, 0.75, "no", 2, 17, 6, 3),
      (0, "female", 32, 1.5, "no", 2, 17, 5, 5),
      (0, "female", 22, 0.75, "no", 2, 12, 1, 3),
      (0, "male", 57, 15, "yes", 2, 14, 4, 4),
      (0, "female", 32, 15, "yes", 4, 16, 1, 2),
      (0, "male", 22, 1.5, "no", 4, 14, 4, 5))

    val data = dataList.toDF("affairs", "gender", "age", "yearsmarried", "children", "religiousness", "education", "occupation", "rating")

    data.show()


    // 操作指定的列,并排序
    data.selectExpr("gender", "age+1 as age1","cast(age as bigint) as age2").sort($"gender".desc, $"age".asc).show

    //dataframe 算子做map操作，操作row类型
    val data1=data.map{case Row(affairs,gender,age,yearsmarried,children,religiousness,education,occupation,rating)=> (affairs.asInstanceOf[Double],gender.asInstanceOf[String].replaceAll("le",""),age.asInstanceOf[Int]+3)}
    data1.show()

  }

}
