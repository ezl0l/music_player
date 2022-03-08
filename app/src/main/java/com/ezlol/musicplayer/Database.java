package com.ezlol.musicplayer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Database {
    public static long addPlaylist(SQLiteDatabase database, Playlist playlist) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", playlist.getName());
        contentValues.put("tracks", Utils.joinTracksId(playlist.getTracks()));

        return database.insert("playlists", null, contentValues);
    }

    public static List<Playlist> getPlaylists(SQLiteDatabase database, List<Track> localTracks) {
        Cursor cursor = database.rawQuery("SELECT * FROM playlists", null);

        List<Track> tracks;
        Playlist playlist;
        List<Playlist> playlists = new ArrayList<>();
        while(cursor.moveToNext()) {
            int[] tracksId = Arrays.stream(cursor.getString(2).split(",")).mapToInt(Integer::parseInt).toArray();

            tracks = new ArrayList<>();
            for(Track track : localTracks) {
                if(IntStream.of(tracksId).anyMatch(x -> x == track.getId())) {
                    tracks.add(track);
                }
            }
            playlist = new Playlist(cursor.getString(1), tracks);
            playlist.setId(cursor.getInt(0));

            playlists.add(playlist);
        }
        return playlists;
    }
}
