package com.vtmn.audioplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.vtmn.audioplayer.ApplicationClass.ACTION_NEXT;
import static com.vtmn.audioplayer.ApplicationClass.ACTION_PLAY;
import static com.vtmn.audioplayer.ApplicationClass.ACTION_PREV;
import static com.vtmn.audioplayer.ApplicationClass.CHANNEL_ID_2;
import static com.vtmn.audioplayer.MainActivity.musicFiles;
import static com.vtmn.audioplayer.MainActivity.playlists;
import static com.vtmn.audioplayer.MainActivity.repeatBoolean;
import static com.vtmn.audioplayer.MainActivity.shuffleBoolean;

public class PlayerActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {
    TextView song_name, artist_name, duration_played, duration_total;
    ImageView img_cover, btnNext, btnPrev, btnBack, btnShuffle, btnRepeat;
    FloatingActionButton btnPlayPause;
    SeekBar seekBar;

    int position = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    //    public static MediaPlayer mediaPlayer;
    static Handler handler = new Handler();
    MusicService musicService;
    MediaSessionCompat mediaSessionCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "Audio");

        initViews();
        getIntentMethod();

//        Seek bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress * 1000);
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
                if (musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean) {
                    shuffleBoolean = false;
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
                } else {
                    shuffleBoolean = true;
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean) {
                    repeatBoolean = false;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_off);
                } else {
                    repeatBoolean = true;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
    }

    //    Bottom Button
    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

        btnPlayThread();
        btnNextThread();
        btnPrevThread();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void btnPrevThread() {
        Thread prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnPrevClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void btnNextThread() {
        Thread nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnNextClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void btnPlayThread() {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnPlayPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnPlayPauseClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void btnPrevClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();

            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            song_name.setText((listSongs.get(position).getTitle()));
            artist_name.setText((listSongs.get(position).getArtist()));

            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_pause);
            btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();

            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            song_name.setText((listSongs.get(position).getTitle()));
            artist_name.setText((listSongs.get(position).getArtist()));

            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_play);
            btnPlayPause.setBackgroundResource(R.drawable.ic_play);
        }
    }

    public void btnPlayPauseClicked() {
        if (musicService.isPlaying()) {
            btnPlayPause.setImageResource(R.drawable.ic_play);
            showNotification(R.drawable.ic_play);
            musicService.pause();

            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            showNotification(R.drawable.ic_pause);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    public void btnNextClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();

            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            position = ((position + 1) % listSongs.size());
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            song_name.setText((listSongs.get(position).getTitle()));
            artist_name.setText((listSongs.get(position).getArtist()));

            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_pause);
            btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();

            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);

            song_name.setText((listSongs.get(position).getTitle()));
            artist_name.setText((listSongs.get(position).getArtist()));

            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_play);
            btnPlayPause.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    //    Format Time
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
        String sender = getIntent().getStringExtra("playlistDetails");
        if (sender != null && sender.equals("playlistDetails")) {
            listSongs = playlists;
        } else {
            listSongs = musicFiles;
        }
        if (listSongs != null) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        showNotification(R.drawable.ic_pause);
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);
    }

    //    Declare variables
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

    //    Get total duration
    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());

        int totalDuration = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(totalDuration));

        byte[] img = retriever.getEmbeddedPicture();
        Bitmap bitmap;

        if (img != null) {
//            Animation background color
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            ImageAnimation(this, img_cover, bitmap);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        ImageView gradient = findViewById(R.id.imgViewGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0x00000000}
                        );
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000}
                        );
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    } else {
                        ImageView gradient = findViewById(R.id.imgViewGradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), 0x00000000}
                        );
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()}
                        );
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.empty)
                    .into(img_cover);

            ImageView gradient = findViewById(R.id.imgViewGradient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }


    //    Image Animation
    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    //    Notification Service

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
        Toast.makeText(this, "Connected" + musicService, Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);

        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        musicService.onCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    //    Show notification
    void showNotification(int btnPlayPause) {
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntend = PendingIntent.getActivity(this, 0, intent, 0);

        Intent preIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prePending = PendingIntent.getBroadcast(this, 0, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap thumb = null;
        byte[] picture = null;
        picture = getAlbumImg(musicFiles.get(position).getPath());
        if (picture != null) {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);

        } else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(btnPlayPause)
                .setLargeIcon(thumb)
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .addAction(R.drawable.ic_skip_previous, "Previous", prePending)
                .addAction(btnPlayPause, "Pause", pausePending)
                .addAction(R.drawable.ic_skip_next, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private byte[] getAlbumImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] img = retriever.getEmbeddedPicture();
        retriever.release();
        return img;
    }
}