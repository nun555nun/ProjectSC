package com.example.projectsc;

public class UserBin {
    String binName;
    String binID;
    String temperature;
    String humidity;
    String date;

    public UserBin(String binName, String binID, String temperature, String humidity, String date) {
        this.binName = binName;
        this.binID = binID;
        this.temperature = temperature;
        this.humidity = humidity;
        this.date = date;
    }

    public String getBinName() {
        return binName;
    }

    public String getBinID() {
        return binID;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getDate() {
        return date;
    }
}
