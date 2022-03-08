package com.ezlol.musicplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistDialog extends DialogFragment implements View.OnClickListener {
    interface OnPlaylistCreateListener {
        void onCreatePlaylist(String playlistName, List<Track> selectedTracks);
    }

    interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }

    interface OnCancelListener {
        void onCancel(DialogInterface dialog);
    }

    private OnPlaylistCreateListener onPlaylistCreateListener = null;
    private OnDismissListener onDismissListener = null;
    private OnCancelListener onCancelListener = null;

    private EditText playlistNameEditText;
    private LinearLayout tracksLayout;

    private List<Track> tracks;

    private Map<Track, CheckBox> trackCheckBoxMap;

    public PlaylistDialog(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.playlist_dialog, null);
        v.findViewById(R.id.yes_btn).setOnClickListener(this);

        playlistNameEditText = v.findViewById(R.id.playlistNameEditText);
        tracksLayout = v.findViewById(R.id.tracksLayout);

        tracksLayout.removeAllViews();

        CheckBox checkBox;
        trackCheckBoxMap = new HashMap<>();
        for(Track track : tracks) {
            checkBox = new CheckBox(getContext());
            checkBox.setText(String.format("%s - %s", track.getAuthor(), track.getTitle()));
            trackCheckBoxMap.put(track, checkBox);
            tracksLayout.addView(checkBox);
        }

        return v;
    }

    public void clearFields() {
        if(trackCheckBoxMap != null) {
            for(CheckBox checkBox : trackCheckBoxMap.values()) {
                checkBox.setChecked(false);
            }
        }
        playlistNameEditText.setText("");
    }

    public void setPlaylistName(String playlistName) {
        playlistNameEditText.setText(playlistName);
    }

    public void setSelectedTracks(List<Track> selectedTracks) {
        if(trackCheckBoxMap == null) return;
        for(Map.Entry<Track, CheckBox> e : trackCheckBoxMap.entrySet()) {
            if(selectedTracks.contains(e.getKey())) {
                e.getValue().setChecked(true);
            }
        }
    }

    @Override
    public void onClick(View view) {
        String playlistName = playlistNameEditText.getText().toString();
        if(playlistName.length() == 0) {
            Toast.makeText(getContext(), "Enter playlist name", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Track> selectedTracks = new ArrayList<>();

        CheckBox checkBox;
        for(Map.Entry<Track, CheckBox> entry : trackCheckBoxMap.entrySet()) {
            checkBox = entry.getValue();

            if(checkBox.isChecked())
                selectedTracks.add(entry.getKey());
        }

        if(selectedTracks.size() == 0) {
            Toast.makeText(getContext(), "Select at least one track", Toast.LENGTH_SHORT).show();
            return;
        }

        if(onPlaylistCreateListener != null)
            onPlaylistCreateListener.onCreatePlaylist(playlistName, selectedTracks);

        getDialog().hide();
        clearFields();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e("Dialog", "Dismiss");

        if(onDismissListener != null)
            onDismissListener.onDismiss(dialog);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        Log.e("Dialog", "Cancel");

        if(onCancelListener != null)
            onCancelListener.onCancel(dialog);
    }

    public void setOnPlaylistCreateListener(OnPlaylistCreateListener onPlaylistCreateListener) {
        this.onPlaylistCreateListener = onPlaylistCreateListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
