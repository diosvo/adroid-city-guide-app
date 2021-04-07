package com.vtmn.audioplayer;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.vtmn.audioplayer.MainActivity.musicFiles;

public class PlaylistDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView playlistImg;
    String playlistName;
    ArrayList<MusicFiles> playlistSongs = new ArrayList<>();
    PlaylistDetailsAdapter playlistDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_details);
        recyclerView = findViewById(R.id.recycleView);
        playlistImg = findViewById(R.id.playlistImg);
        playlistName = getIntent().getStringExtra("playlistName");
        int j = 0;
        for (int i = 0; i < musicFiles.size(); i++) {
            if (playlistName.equals(musicFiles.get(i).getAlbum())) {
                playlistSongs.add(j, musicFiles.get(i));
                j++;
            }
        }
        byte[] image = getPlaylistImg(playlistSongs.get(0).getPath());
        if (image != null) {
            Glide.with(this)
                    .load(image)
                    .into(playlistImg);
        } else {
            Glide.with(this)
                    .load(R.drawable.empty)
                    .into(playlistImg);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playlistSongs.size() < 1) {
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(playlistDetailsAdapter);
//            playlistDetailsAdapter = new PlaylistDetailsAdapter(this, playlistSongs);
//            recyclerView.setAdapter(playlistDetailsAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }

    private byte[] getPlaylistImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] img = retriever.getEmbeddedPicture();
        retriever.release();
        return img;
    }
}