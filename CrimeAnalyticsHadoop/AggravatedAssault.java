/*
Name: Praveen Ramesh
Andrew ID:  pramesh2@andrew.cmu.edu
AggravatedAssault is driver for a Hadoop MapReduce application that processes crime data
to calculate the total count of aggravated assault within 350m
from 3803 Forbes Avenue in Oakland.
 */
package edu.cmu.andrew.student119;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;

public class AggravatedAssault {
    /*
        The main method serves as the entry point for the application. It sets up the job
        * configuration and starts the job execution.
        */
    public static void main(String[] args) throws Exception {
        // Check if the correct number of arguments is provided
        if (args.length != 2) {
            System.err.println("Usage: AggravatedAssault <input path> <output path>");
            System.exit(-1);
        }
        // Create a new JobConf object for configuring the job
        JobConf conf = new JobConf(AggravatedAssault.class);
        conf.setJobName("Aggravated Assault");

        // Set the input and output paths for the job using the command-line arguments
        FileInputFormat.addInputPath(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        // Set the mapper and reducer classes for the job
        conf.setMapperClass(AggravatedAssaultMapper.class);
        conf.setReducerClass(AggravatedAssaultReducer.class);

        // Set the key and value types for the map output
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(IntWritable.class);

        // Set the key and value types for the final job output
        conf.setOutputKeyClass(NullWritable.class);
        conf.setOutputValueClass(IntWritable.class);

        // Start the job and wait for it to complete
        JobClient.runJob(conf);
    }
}
