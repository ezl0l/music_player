package com.ezlol.musicplayer;

import java.util.List;
import java.util.Locale;

public class Utils {
    public static String beautifyTime(int seconds) {
        return String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
    }

    public static String joinTracksId(List<Track> tracks, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        for(Track track : tracks) {
            stringBuilder.append(track.getId()).append(delimiter);
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public static String joinTracksId(List<Track> tracks) {
        return joinTracksId(tracks, ",");
    }
}
