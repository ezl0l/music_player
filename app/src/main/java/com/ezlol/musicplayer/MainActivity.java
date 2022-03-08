package com.ezlol.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, PlaylistDialog.OnPlaylistCreateListener,
        PlaylistDialog.OnDismissListener, PlaylistDialog.OnCancelListener {
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    private PlaylistAdapter playlistAdapter;
    private TrackAdapter trackAdapter;

    private ListView tracksListView;
    private LinearLayout BCTLayout;
    private TextView BCTName, BCTAuthor;
    private ImageView BCTPlayBtn, BCTPauseBtn, BCTNextBtn;
    private HorizontalScrollView playlistsScrollView;
    private LinearLayout playlistsLayout;

    private PlaylistDialog playlistDialog;

    private DatabaseHelper dbHelper;

    private int trackCursor;

    final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this, "db", 1);

        BCTLayout = findViewById(R.id.bottomCurrentTrack);

        BCTName = findViewById(R.id.BCTName);
        BCTAuthor = findViewById(R.id.BCTAuthor);

        BCTPlayBtn = findViewById(R.id.BCTPlayBtn);
        BCTPauseBtn = findViewById(R.id.BCTPauseBtn);
        BCTNextBtn = findViewById(R.id.BCTNextBtn);

        BCTPlayBtn.setOnClickListener(this);
        BCTPauseBtn.setOnClickListener(this);
        BCTNextBtn.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            return;
        }

        List<Track> localTracks = getLocalTracks();

        playlistDialog = new PlaylistDialog(localTracks);
        playlistDialog.setOnPlaylistCreateListener(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        playlistAdapter = new PlaylistAdapter(this, new ArrayList<Playlist>(){{
            add(new Playlist("From phone", localTracks));
            addAll(Database.getPlaylists(db, localTracks));
        }});

        db.close();

        trackAdapter = playlistAdapter.get(0).getTrackAdapter(this);

        tracksListView = findViewById(R.id.tracks);
        tracksListView.setOnItemClickListener(this);
        tracksListView.setAdapter(trackAdapter);

        playlistsScrollView = findViewById(R.id.playlists);

        playlistsLayout = new LinearLayout(this);
        playlistsLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        playlistsLayout.setOrientation(LinearLayout.HORIZONTAL);

        updatePlaylists();
    }

    private void updateTrackAdapter(TrackAdapter trackAdapter) {
        this.trackAdapter = trackAdapter;
        tracksListView.setAdapter(trackAdapter);
    }

    private void updatePlaylists() {
        Map<Playlist, View> playlistViewMap = playlistAdapter.fillView(playlistsLayout);

        View view;
        for(Map.Entry<Playlist, View> entry : playlistViewMap.entrySet()) {
            Playlist playlist = entry.getKey();
            view = entry.getValue();

            view.setOnClickListener(v -> updateTrackAdapter(playlist.getTrackAdapter(MainActivity.this)));
        }

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.playlistImageHeight),
                (int) getResources().getDimension(R.dimen.playlistImageWidth)));

        ImageView createPlaylistBtn = new ImageView(this);
        createPlaylistBtn.setImageResource(R.drawable.ic_plus_icon);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.createPlaylistBtnWidth),
                (int) getResources().getDimension(R.dimen.createPlaylistBtnHeight));
        createPlaylistBtn.setLayoutParams(layoutParams);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DIALOG", "1");
                try {
                    if(playlistDialog.isAdded())
                        playlistDialog.getDialog().show();
                    else
                        playlistDialog.show(getSupportFragmentManager(), "dialog");
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                Log.d("DIALOG", "2");
            }
        });

        linearLayout.addView(createPlaylistBtn);
        playlistsLayout.addView(linearLayout);

        playlistsScrollView.removeAllViews();
        playlistsScrollView.addView(playlistsLayout);
    }

    private List<Track> getLocalTracks() {
        List<Track> tracks = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.AudioColumns.DURATION, MediaStore.Audio.AudioColumns._ID};

        Cursor songCursor = contentResolver.query(songUri, projection, null, null, null);
        if (songCursor != null) {
            while (songCursor.moveToNext()) {
                String currentPath = songCursor.getString(0);
                String currentTitle = songCursor.getString(1);
                String currentArtist = songCursor.getString(2);
                int currentDuration = songCursor.getInt(3);
                int currentID = songCursor.getInt(4);
                if(currentArtist.equals("<unknown>")) {
                    String delimiter = null;
                    if(currentTitle.contains("-")) {
                        delimiter = "-";
                    } else if(currentTitle.contains("—")) {
                        delimiter = "—";
                    }

                    if(delimiter != null) {
                        String[] splitted = currentTitle.split(delimiter);

                        currentArtist = splitted[0].trim();
                        currentTitle = splitted[1].trim();
                    }
                }
                Log.d("My", currentTitle + " " + currentID);
                tracks.add(new Track(currentID, currentDuration / 1000f, currentTitle, currentArtist, currentPath));
            }
        } else
            Log.d("My", "NULL");
        return tracks;
    }

    private void setTrack(int i) {
        trackCursor = i;

        Track track = (Track) trackAdapter.getItem(i);
        BCTName.setText(track.getTitle());
        BCTAuthor.setText(track.getAuthor());

        setTrack(track.getPath());
    }

    private void setTrack(String path) {
        BCTLayout.setVisibility(View.VISIBLE);

        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            Toast.makeText(this, R.string.track_loading_error, Toast.LENGTH_SHORT).show();
            return;
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.prepareAsync();
    }

    private void play() {
        if(!mediaPlayer.isPlaying())
            mediaPlayer.start();
        BCTPlayBtn.setVisibility(View.GONE);
        BCTPauseBtn.setVisibility(View.VISIBLE);
    }

    private void pause() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        BCTPlayBtn.setVisibility(View.VISIBLE);
        BCTPauseBtn.setVisibility(View.GONE);
    }

    private void next() {
        if(trackCursor + 1 >= trackAdapter.getCount())
            return;
        setTrack(++trackCursor);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        setTrack(i);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        play();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(this, "permission failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.BCTPlayBtn:
                play();
                break;

            case R.id.BCTPauseBtn:
                pause();
                break;

            case R.id.BCTNextBtn:
                next();
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        next();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onCreatePlaylist(String playlistName, List<Track> selectedTracks) {
        Playlist newPlaylist = new Playlist(playlistName, selectedTracks);

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        long id = Database.addPlaylist(sqLiteDatabase, newPlaylist);
        sqLiteDatabase.close();

        newPlaylist.setId(id);

        playlistAdapter.add(newPlaylist);

        updatePlaylists();
    }
}