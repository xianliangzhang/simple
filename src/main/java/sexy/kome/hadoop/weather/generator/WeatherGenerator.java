package sexy.kome.hadoop.weather.generator;

import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hack on 2016/11/20.
 * 该类用于生成测试数据
 * 1950010101 STATION01 23 87
 */
public class WeatherGenerator {
    // 记录气象有效期
    private static final int YEAR_START = 2000;
    private static final int YEAR_RANGE = 10;

    // 温度变化范围
    private static final int TEMPERATURE_RANGE_MIN = -50;
    private static final int TEMPERATURE_RANGE = 100;

    // 湿度变化范围
    private static final int HUMIDITY_RANGE_MIN = -100;
    private static final int HUMIDITY_RANGE = 200;


    private static class Generator extends Thread {
        private WeatherStation station;

        public Generator(WeatherStation station) {
            this.station = station;
        }

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(YEAR_START, 01, 01);
            Date date = calendar.getTime();

            do {
                date = DateUtils.addDays(generateRecordForDay(date), 1);
            } while (date.before(new Date()));
        }

        private Date generateRecordForDay(Date date) {
            for (int i = 0; i < 24; i ++) {
                String datetime = new SimpleDateFormat("YYYYMMdd").format(date).concat(String.valueOf(i));
                int temperature = (int) (Math.random() * TEMPERATURE_RANGE + TEMPERATURE_RANGE_MIN);
                int humidity = (int) (Math.random() * HUMIDITY_RANGE + HUMIDITY_RANGE_MIN);
                WeatherRecord record = new WeatherRecord(datetime, station, temperature, humidity);
                WeatherLogger.log(record);
            }
            return date;
        }
    }

    public static void main(String[] args) {
        for (WeatherStation station : WeatherStation.values()) {
            new Generator(station).start();
        }
        WeatherLogger.stopWrite();
    }
}
