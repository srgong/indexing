import org.apache.spark.{SparkConf}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
  * Created by Sharon on 3/4/19.
  */
object Driver {

  val sparkConf = new SparkConf()
    .setAppName("Indexing Books")
    .setMaster("local[*]")
    .set("spark.input.file", "src/main/resources/")
    .set("spark.output.file", "src/main/output/")

  def main(args: Array[String]): Unit = {
    val spark: SparkSession =
      SparkSession.builder().config(sparkConf).getOrCreate()
    val sqlContext = spark.sqlContext

    val input = sparkConf.get("spark.input.file")

    import spark.implicits._
     val df = sqlContext.read.text(input).toDF()
          .select(
//             extract only the last numbers from file path
            regexp_extract(input_file_name,"([0-9]*)$",1) as "docId",
//             normalize words
            lower(col("value")) as "wordId")
//           create key value pair
          .as[(String, String)].rdd
          // break each line into its own word
          .flatMapValues(line => line.split("[^\\w]"))
          // set the key to the word
          .keyBy(x => x._2) // key by docId
          // rearrange tuple to just (docId, wordId)
          .mapValues(x => x._1) // map docId -> wordId
          // remove duplicate key value pairs
          .distinct
          // create arrays where there are similar keys
          .groupByKey()

    // using data frames to set up wordId, docId
    //    val df = sqlContext.read.text(input).toDF()
    //      .select(explode(split(lower(col("value")), "[^\\w]") as "wordId"),regexp_extract(input_file_name,"([0-9]*)$",1) as "docId")
    //      .distinct
    //      .as[(String, String)].rdd.groupByKey()


    df.repartition(255).saveAsTextFile(sparkConf.get("spark.output.file"))
  }
}
