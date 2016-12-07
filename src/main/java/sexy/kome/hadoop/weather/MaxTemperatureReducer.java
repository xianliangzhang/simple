package sexy.kome.hadoop.weather;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by Hack on 2016/11/20.
 */
public class MaxTemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int maxTemperature = Integer.MIN_VALUE;

        for (IntWritable value : values) {
            if (value.get() > maxTemperature) {
                System.out.println(String.format("  -- Reducer [year=%s, temperature=%s]", key, value.get()));
                maxTemperature = value.get();
            }
        }

        context.write(key, new IntWritable(maxTemperature));
    }
}
