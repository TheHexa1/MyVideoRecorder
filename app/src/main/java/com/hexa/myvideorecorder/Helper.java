package com.hexa.myvideorecorder;

import java.util.concurrent.TimeUnit;

public class Helper {

        public static String getFormattedDuration(int duration){
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
        }
}
