package com.example.projectsc;

public class LogNotification {
    String type;
    String date;
    String time;

    public LogNotification(){

    }

    public LogNotification(String type, String date, String time) {
        this.type = type;
        this.date = date;
        this.time = time;
    }

    public String getType() {
        return type;
    }


    public String getDate() {
        return date;
    }


    public String getTime() {
        return time;
    }

    public void setType(String type) {
        this.type = type;
    }
}
