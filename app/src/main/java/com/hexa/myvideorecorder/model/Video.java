package com.hexa.myvideorecorder.model;

import android.media.Image;

import java.util.Date;

public class Video {

    private String name;
    private int duration; //milliseconds
    private Date timeStamp;
    private String path;

    public Video(String name, int duration, Date timeStamp){
        this.name = name;
        this.duration = duration;
        this.timeStamp = timeStamp;
//        this.path = path;
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

    public String getPath() {
        return path;
    }

}
