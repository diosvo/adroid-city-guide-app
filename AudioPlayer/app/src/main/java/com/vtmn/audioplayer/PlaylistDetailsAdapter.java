package com.vtmn.audioplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class PlaylistDetailsAdapter extends RecyclerView.Adapter<PlaylistDetailsAdapter.MyHolder> {
    private final Context mContext;
    private final ArrayList<MusicFiles> playlistFiles;
    View view;

    public PlaylistDetailsAdapter(Context mContext, ArrayList<MusicFiles> playlistFiles) {
        this.mContext = mContext;
        this.playlistFiles = playlistFiles;
    }

    @NonNull
    @Override
    public PlaylistDetailsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistDetailsAdapter.MyHolder holder, int position) {
        holder.playlist_name.setText(playlistFiles.get(position).getTitle());
        byte[] image = getAlbumImg(playlistFiles.get(position).getPath());

        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.playlist_img);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.empty)
                    .into(holder.playlist_img);
        }
    }

    @Override
    public int getItemCount() {
        return playlistFiles.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        ImageView playlist_img;
        TextView playlist_name;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            playlist_img = itemView.findViewById(R.id.music_img);
            playlist_name = itemView.findViewById(R.id.playlist_name);
        }
    }

    private byte[] getAlbumImg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] img = retriever.getEmbeddedPicture();
        retriever.release();
        return img;
    }
}
