package com.vtmn.audioplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.vtmn.audioplayer.MainActivity.musicFiles;

public class PlayerActivity extends AppCompatActivity {
    TextView song_name, artist_name, duration_played, duration_total;
    ImageView img_cover, btnNext, btnPrev, btnBack, btnShuffle, btnRepeat;
    FloatingActionButton btnPlayPause;
    SeekBar seekBar;

    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    static  Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        getIntentMethod();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private String formattedTime(int mCurrentPosition) {
        String totalOld = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOld = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOld;
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        listSongs = musicFiles;
        if (listSongs != null) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.artist_name);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);

        img_cover = findViewById(R.id.img_cover);
        btnNext = findViewById(R.id.ic_next);
        btnPrev = findViewById(R.id.ic_prev);
        btnBack = findViewById(R.id.ic_back);
        btnShuffle = findViewById(R.id.ic_shuffle);
        btnRepeat = findViewById(R.id.ic_repeat);

        btnPlayPause = findViewById(R.id.ic_play_pause);
        seekBar = findViewById(R.id.seekBar);
    }
}