
# Table of Contents
1. [Introduction](README.md#introduction)
2. [Approach](README.md#approach)
3. [To Run](README.md#torun)


# Introduction

This project addresses this [prompt](https://github.com/Samariya57/coding_challenges/blob/master/challenge.pdf), where we build a pipeline that creates the index portion commonly found in books. Given text input, this pipeline transforms it into a key value pair where the key is a word, and the value is the file name the word can be found in.

# Approach

Given text files, the objective is to create a lookup table of words mapped to files they're found in.  

1) Text files are read in as dataframes where we append each line with their file names using `input_file_name()`.


# To Run
1. `sbt assembly`
2. `spark-submit Driver spark.input.file=<input_file> spark.output.file=<output_file> <jar_file>`
You can find the jar file here: `target/scala-2.11/indexing-assembly-1.0.jar`

 
