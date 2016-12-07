package sexy.kome.hadoop.weather.generator;

import java.io.FileWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Hack on 2016/11/20.
 */
public class WeatherLogger {

    private static final BlockingQueue<WeatherRecord> QUEUE = new ArrayBlockingQueue<WeatherRecord>(100);
    private static final String DEFAULT_LOG_FILE_NAME = "/Users/Hack/lab/weather.log";
    private static String fileName = DEFAULT_LOG_FILE_NAME;
    private static final Thread WRITE_LOG_THREAD = new WriteLogThread();

    private WeatherLogger() {
    }

    public static final void log(WeatherRecord record) {
        QUEUE.offer(record);
        System.out.println("++" + Thread.currentThread().getId()+ " " + QUEUE.size());

        if (!WRITE_LOG_THREAD.isAlive()) {
            WRITE_LOG_THREAD.start();
        }
    }

    private static class WriteLogThread extends Thread {
        @Override
        public void run() {
            try {
                FileWriter writer = new FileWriter(fileName, true);
                while (!Thread.currentThread().isInterrupted()) {
                    writer.write(QUEUE.take().toString().concat("\n"));
                    System.out.println("--" + Thread.currentThread().getId()+ " " + QUEUE.size());
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopWrite() {
        while (!QUEUE.isEmpty()) {
            try {
                Thread.sleep(1000);
                System.out.println("Waiting for consume WeatherLog - " + QUEUE.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!WRITE_LOG_THREAD.isInterrupted()) {
            WRITE_LOG_THREAD.interrupt();
        }
    }

    public static void main(String[] args) {
        WeatherLogger.log(new WeatherRecord("xx", WeatherStation.BEIJING, 2, 1));
    }
}
