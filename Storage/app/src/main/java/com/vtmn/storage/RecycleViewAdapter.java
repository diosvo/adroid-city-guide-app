package com.vtmn.storage;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;

public class RecycleViewAdapter extends Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;

    RecycleViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list, parent, false);
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FileLayoutHolder) holder).video_title.setText(Constant.allMediaList.get(position).getName());

//        Using Glide library to load files
        Uri uri = Uri.fromFile(Constant.allMediaList.get(position));
        Glide.with(mContext).load(uri).into(((FileLayoutHolder) holder).thumbnail);
    }

    @Override
    public int getItemCount() {
        return Constant.allMediaList.size();
    }

    class FileLayoutHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail;
        TextView video_title;
        ImageButton ic_more_btn;

        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            video_title = itemView.findViewById(R.id.video_title);
            ic_more_btn = itemView.findViewById(R.id.ic_more_btn);
        }
    }
}
