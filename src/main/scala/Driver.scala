import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

/**
  * Created by Sharon on 3/4/19.
  */
object Driver {

  val sparkConf = new SparkConf()
    .setAppName("Indexing Books")
    .set("spark.input.file", "src/main/resources/")
    .set("spark.output.file", "src/main/output/")

  /*
   * Param: DataFrame
   * Output: RDD of <wordId, docId>
   *
   * DataFrames are processed by following this logic:
   * Extract only the last numbers from the file path
   * Normal words
   * Create key value pair
   * Break each line into its own word
   * Set the key to the word
   * Key by docId
   * Rearrange tuple to just (docId, wordId)
   * Map docId to wordId
   * Remove duplicate key value pairs
   * Create arrays where there are similar keys
   */
  def createIndex(df: DataFrame): RDD[(String, Iterable[String])] = {
    df.select(
      regexp_extract(input_file_name,"([0-9]*)$",1) as "docId",
      lower(col("value")) as "wordId")
      .as[(String, String)].rdd
      .flatMapValues(line => line.split("[^\\w]"))
      .keyBy(x => x._2)
      .mapValues(x => x._1)
      .distinct
      .groupByKey()
  }

  def main(args: Array[String]): Unit = {
    val spark: SparkSession =
      SparkSession.builder().config(sparkConf).getOrCreate()
    val sqlContext = spark.sqlContext

    val input = sparkConf.get("spark.input.file")
    val df = createIndex(sqlContext.read.text(input).toDF())

    df.repartition(255).saveAsTextFile(sparkConf.get("spark.output.file"))
  }
}
