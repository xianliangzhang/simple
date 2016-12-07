package sexy.kome.hadoop.weather;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by Hack on 2016/11/20.
 */
public class MaxTemperatureMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
        String line = value.toString();
        String[] record = line.split(" ");
        String year = record[0].substring(0, 4);
        String temperature = record[3];
        System.out.println(String.format(" -- Mapper [year=%s, temperature=%s]", year, temperature));

        context.write(new Text(year), new IntWritable(new Integer(temperature)));
    }
}
