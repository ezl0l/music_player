package com.ezlol.musicplayer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistAdapter {
    private final Context context;
    private List<Playlist> playlists;

    public PlaylistAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    public Map<Playlist, View> fillView(ViewGroup view) {
        view.removeAllViews();

        Map<Playlist, View> viewMap = new HashMap<>();
        View playlistView;
        for(Playlist playlist : playlists) {
            playlistView = playlist.getView(context);
            view.addView(playlistView);

            viewMap.put(playlist, playlistView);
        }

        return viewMap;
    }

    public int getCount() {
        return playlists.size();
    }

    public Playlist get(int i) {
        return playlists.get(i);
    }

    public boolean add(Playlist playlist) {
        return playlists.add(playlist);
    }

    public boolean addAll(Collection<Playlist> playlists) {
        return this.playlists.addAll(playlists);
    }

    public void update(Collection<Playlist> playlists) {
        this.playlists = (List<Playlist>) playlists;
    }
}
