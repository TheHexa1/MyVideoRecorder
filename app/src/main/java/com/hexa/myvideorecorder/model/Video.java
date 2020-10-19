package com.hexa.myvideorecorder.model;

import java.util.Date;

public class Video {

    private String name;
    private int duration; //milliseconds
    private Date timeStamp;
    private String videoURI;

    public Video(String name, int duration, Date timeStamp, String vPath){
        this.name = name;
        this.duration = duration;
        this.timeStamp = timeStamp;
        this.videoURI = vPath;
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

    public String getVideoURI() {
        return videoURI;
    }
}
