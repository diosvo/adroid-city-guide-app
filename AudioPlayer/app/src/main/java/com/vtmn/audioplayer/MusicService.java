package com.vtmn.audioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.vtmn.audioplayer.PlayerActivity.listSongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;

    ActionPlaying actionPlaying;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        if (myPosition != -1) {
            playMedia(myPosition);
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        musicFiles = listSongs;
        position = startPosition;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (musicFiles != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        } else {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start() {
        mediaPlayer.start();
    }

    void pause() {
        mediaPlayer.pause();
    }

    void stop() {
        mediaPlayer.stop();
    }

    void release() {
        mediaPlayer.release();
    }

    boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    int getDuration() {
        return mediaPlayer.getDuration();
    }

    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    void createMediaPlayer(int position) {
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    void onCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null) {
            actionPlaying.btnNextClicked();
        }
        createMediaPlayer(position);
        start();
        onCompleted();
    }
}
