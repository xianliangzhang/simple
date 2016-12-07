package sexy.kome.hadoop.weather.generator;

/**
 * Created by Hack on 2016/11/20.
 */
public class WeatherRecord {
    public int temperature;          // 温度 -摄氏度
    public int humidity;             // 湿度
    public String datetime;          // 记录时间,格式 yyyyMMddHH
    public WeatherStation station;   // 气象站点标识

    public WeatherRecord(String datetime, WeatherStation station, int temperature, int humidity) {
        this.station = station;
        this.datetime = datetime;
        this.humidity = humidity;
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d %d", datetime, station, temperature, humidity);
    }
}
