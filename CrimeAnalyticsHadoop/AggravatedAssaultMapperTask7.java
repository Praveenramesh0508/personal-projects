/*
Name: Praveen Ramesh
Andrew ID:  pramesh2@andrew.cmu.edu
AggravatedAssaultMapperTask7 is a Hadoop Mapper implementation that filters aggravated assault incidents
within 350m from 3803 Forbes Avenue in Oakland and emits the
latitude and longitude of the incidents.
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
// Define the AggravatedAssaultMapperTask7 class that extends the MapReduceBase and implements the Mapper interface
public class AggravatedAssaultMapperTask7 extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
    //Initialize the coordinates and threshold
    private static final double xForbes = 1354326.897;
    private static final double yForbes = 411447.7828;
    private static final double threshold = 1148.29; // 350 meters in feet
    //Initialize a text with location
    private Text Key = new Text("location");
    //Override the map function to define the mapping logic to emit lat and long of aggravated assault within the radius
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        //Get the line
        String line = value.toString();
        //Split the line data
        String[] data = line.split("\t");
        //get the offense type
        String offenseType = data[4];
        //check if the offense is AGGRAVATED ASSAULT
        if ("AGGRAVATED ASSAULT".equals(offenseType)) {
            try {
                //Get the coordinates x and y
                double x = Double.parseDouble(data[0]);
                double y = Double.parseDouble(data[1]);
                String latitude = data[7];
                String longitude = data[8];
                //calculate the distance
                double distance = Math.sqrt(Math.pow(x - xForbes, 2) + Math.pow(y - yForbes, 2));
                //check if the distance is within the threshold
                if (distance <= threshold) {
                    //Collect the offenseTypeText, one pair to the output
                    output.collect(Key, new Text(latitude + "," + longitude));
                }
            } catch (NumberFormatException e) {
            }
        }
    }
}

