/*
Name: Praveen Ramesh
Andrew ID:  pramesh2@andrew.cmu.edu
AggravatedAssaultMapper is a Hadoop Mapper implementation that processes input
crime data records and emits the count of aggravated assault incidents within 350m
from 3803 Forbes Avenue in Oakland.
 */
package edu.cmu.andrew.student119;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

// Define the AggravatedAssaultMapper class that extends the MapReduceBase and implements the Mapper interface
public class AggravatedAssaultMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
    //Initialize the coordinates and threshold
    private static final double xForbes = 1354326.897;
    private static final double yForbes = 411447.7828;
    private static final double threshold = 1148.29;
    //Initialize a static IntWritable
    private final static IntWritable one = new IntWritable(1);
    //Initialize a text offenseTypeText
    private Text offenseTypeText = new Text("Aggravated Assault");
    //Override the map function to define the mapping logic for counting aggravated assault within the radius
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
    {
        //Get the line
        String line = value.toString();
        //Split the line data
        String[] data = line.split("\t");
        //get the offense type
        String offenseType = data[4];
        //check if the offense is AGGRAVATED ASSAULT
        if ("AGGRAVATED ASSAULT".equals(offenseType))
        {   //Get the coordinates x and y
            try {
                double x = Double.parseDouble(data[0]);
                double y = Double.parseDouble(data[1]);
                //calculate the distance
                double distance = Math.sqrt(Math.pow(x - xForbes, 2) + Math.pow(y - yForbes, 2));
                //check if the distance is within the threshold
                if (distance <= threshold) {
                    //Collect the offenseTypeText, one pair to the output
                    output.collect(offenseTypeText,one);
                }
            } catch (NumberFormatException e) {
            }
            }

    }
}
