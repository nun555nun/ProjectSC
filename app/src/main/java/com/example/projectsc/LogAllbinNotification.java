package com.example.projectsc;

public class LogAllbinNotification {
    private String binName;
    private String binId;
    private String date;
    private String time;
    private String type;

    public LogAllbinNotification() {
    }

    public LogAllbinNotification(String binName, String binID, String date, String time, String type) {
        this.binName = binName;
        this.binId = binID;
        this.date = date;
        this.time = time;
        this.type = type;
    }

    public String getBinName() {
        return binName;
    }

    public String getBinID() {
        return binId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }
}

