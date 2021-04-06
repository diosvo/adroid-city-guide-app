package com.vtmn.audioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.vtmn.audioplayer.MainActivity.musicFiles;

public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    PlaylistAdapter playlistAdapter;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        if (!(musicFiles.size() < 1)) {
            playlistAdapter = new PlaylistAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(playlistAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        return view;
    }
}