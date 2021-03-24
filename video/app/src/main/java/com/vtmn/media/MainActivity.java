package com.vtmn.media;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final VideoView myVideoView = (VideoView) findViewById(R.id.videoView);
        myVideoView.setVideoPath("https://cdn.videvo.net/videvo_files/video/free/2020-10/small_watermarked/200910_02_Oxford_4K_049_preview.webm");
        myVideoView.start();
    }
}