package com.example.projectsc;

public class LogDHT {
    String temperature;
    String humidity;
    String date;
    String time;


    public LogDHT() {

    }

    public LogDHT(String temperature, String humidity,String date, String time) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.date = date;
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
