package com.ezlol.musicplayer;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import java.util.List;

public class Playlist {
    private long id;
    private String name;
    @DrawableRes private int albumResId;
    private List<Track> tracks;

    public Playlist(String name, int albumResId, List<Track> tracks) {
        this.id = id;
        this.name = name;
        this.albumResId = albumResId;
        this.tracks = tracks;
    }

    public Playlist(String name, List<Track> tracks) {
        this.id = id;
        this.name = name;
        this.tracks = tracks;
    }

    public View getView(Context c) {
        LinearLayout layout = new LinearLayout(c);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((int) c.getResources().getDimension(R.dimen.playlistLayoutLeftMargin), 0, 0, 0);

        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView image = new ImageView(c);
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        image.getLayoutParams().height = (int) c.getResources().getDimension(R.dimen.playlistImageHeight);
        image.getLayoutParams().width = (int) c.getResources().getDimension(R.dimen.playlistImageWidth);
        if(albumResId == 0)
            image.setImageResource(R.drawable.ic_default_track_album);
        else
            image.setImageResource(albumResId);

        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textLayoutParams.setMargins(0, (int) c.getResources().getDimension(R.dimen.playlistTextTopMargin), 0, 0);

        TextView textView = new TextView(c);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textLayoutParams);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(name);

        layout.addView(image);
        layout.addView(textView);

        return layout;
    }

    public TrackAdapter getTrackAdapter(Context c) {
        return new TrackAdapter(c, tracks);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAlbumResId() {
        return albumResId;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setId(long id) {
        this.id = id;
    }
}
