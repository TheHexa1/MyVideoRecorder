package com.hexa.myvideorecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import java.io.File;

public class VideoPlayer extends AppCompatActivity {

    VideoView videoView;
    String videoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        if(getIntent().getExtras() != null) {
            Intent i = getIntent();
            videoURI = i.getStringExtra("videoURI");
        }
        videoView = findViewById(R.id.videoPlayer);
        videoView.setVideoURI(Uri.fromFile(new File(videoURI)));
        videoView.start();
    }
}