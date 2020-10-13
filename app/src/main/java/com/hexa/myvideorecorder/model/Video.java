package com.hexa.myvideorecorder.model;

import java.util.Date;

public class Video {

    private String name;
    private int duration; //milliseconds
    private Date timeStamp;

    public Video(String name, int duration, Date timeStamp){
        this.name = name;
        this.duration = duration;
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

}
