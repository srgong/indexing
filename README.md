
# Table of Contents
1. [Introduction](README.md#introduction)
2. [Approach](README.md#approach)
3. [To Run](README.md#torun)


# Introduction

This project addresses this [prompt](https://github.com/Samariya57/coding_challenges/blob/master/challenge.pdf), where we build a pipeline that creates the index portion commonly found at the end of textbooks. Given text input, this pipeline transforms it into a key value pair where the key is a word, and the value is a list of file names the word can be found in.

# Approach

Given text files, the objective is to create a lookup table of words mapped to files they're found in.  

1) Text files are read in as Dataframes. This dataframe contains a single value column for each line of the text. We append each line with their file names using `input_file_name()`.
2) Next, we normalize the data. File names are cleaned by extracting the trailing number id to create `docId` and each line of text is set to lowercase.
3) We restructure the dataframe into a RDD, where key value pairs are `<docId, line>` for mapping manipulation.
4) We split each line into individual line arrays: `<docId, [wordId1, wordId2...]> `
5) We flatten everything. `<docId, wordId1>, <docId, wordId2> ...`
5) We key by wordId now, so a row in the RDD looks like this: `<wordId, (docId, wordId)>`
5) In the next map step, we extract the docId only: `<wordId, docId>`
6) Next we remove duplicate key value pairs.
7) Lastly, we create our index by grouping by wordIds: `<wordId, [docId1, docId2 ...]`


# To Run
1. `sbt assembly`
2. `spark-submit Driver --master spark://<master_node>:7077 --conf spark.input.file=<input_file> --conf spark.output.file=<output_file> <jar_file>`

You can find the jar file here: `target/scala-2.11/indexing-assembly-1.0.jar`

 
