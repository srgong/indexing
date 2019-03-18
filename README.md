
# Table of Contents
1. [Introduction](README.md#introduction)
2. [Approach](README.md#approach)
3. [Considerations](README.md#considerations)
4. [To Run](README.md#torun)


# Introduction

This project addresses this [prompt](https://github.com/Samariya57/coding_challenges/blob/master/challenge.pdf), where we build a pipeline that creates the index portion commonly found at the end of textbooks. Given text input, this pipeline transforms it into a key value pair where the key is a word, and the value is a list of file names the word can be found in.

# Approach

Given text files, the objective is to create a lookup table of words mapped to files they're found in. This is done using DataFrames to extract file names, and then RDD manipulation to coerce the data into desired output. 

1) Text files are read in as Dataframes. This dataframe contains a single value column for each line of the text. We append each line with their file names using `input_file_name()`.
2) Next, we normalize the data. File names are cleaned by extracting the trailing number id to create `docId` and each line of text is set to lowercase.
3) We restructure the dataframe into a RDD, where key value pairs are `<docId, line>` for mapping manipulation.
4) We split each line into individual line arrays: `<docId, [wordId1, wordId2...]> `
5) We flatten everything. `<docId, wordId1>, <docId, wordId2> ...`
5) We key by wordId now, so a row in the RDD looks like this: `<wordId, (docId, wordId)>`
5) In the next map step, we extract the docId only: `<wordId, docId>`
6) Next we remove duplicate key value pairs.
7) Lastly, we create our index by grouping by wordIds: `<wordId, [docId1, docId2 ...]`

# Considerations

Processing data in RDDs has a reputation of taking more processing time - especially because data gets [shuffled into the Driver first](https://dzone.com/articles/apache-spark-3-reasons-why-you-should-not-use-rdds) before transformations can be done. I wanted to explore an alternate method of performing this indexing operation using DataFrames to test for potential performance increase. Previously, I believed general performance increase is not fully dependent on the data structure (RDDs vs DataFrames), but the operations done on the data structure. I believe that column operations using DataFrames were most optimal whereas RDDs were best for row level manipulations. 

`//using data frames to set up wordId, docId`

`val df = sqlContext.read.text(input).toDF()
.select(explode(split(lower(col("value")), "[^\\w]") as "wordId"),regexp_extract(input_file_name,"([0-9]*)$",1) as "docId")
.distinct
.as[(String, String)].rdd.groupByKey()`

In my alternate method, the main difference is how I chose to break out the column of arrays using DataFrames `explode` instead of RDDs `flatMap`. In testing for performance increase, `explode` took longer (I imagine by orders of magnitude depending on data size). While DataFrame operations may have a better performance when calculating aggregations (ie count, sum, max), row level operations are still done better in RDDs (flatMap, keyBy).  Note that I am using Spark 2.3.1 so this [explode optimization](https://issues.apache.org/jira/browse/SPARK-21657) does not apply.   

# To Run
1. `sbt assembly`
2. `spark-submit Driver --master spark://<master_node>:7077 --conf spark.input.file=<input_file> --conf spark.output.file=<output_file> <jar_file>`

You can find the jar file here: `target/scala-2.11/indexing-assembly-1.0.jar`

 
