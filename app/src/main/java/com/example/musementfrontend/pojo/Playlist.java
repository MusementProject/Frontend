package com.example.musementfrontend.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public class Playlist implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    private int id;
    @Getter
    private String title;
    @Getter
    private String playlistUrl;
    private Map<Artist, Integer> artistSongCounts;

    public Playlist(int id, String title, String playlistUrl) {
        this.id = id;
        this.title = title;
        this.playlistUrl = playlistUrl;
        this.artistSongCounts = new HashMap<>();
    }

    public Playlist(int id, String title, String playlistUrl, List<PlaylistInfo> playlistInfos) {
        this.id = id;
        this.title = title;
        this.playlistUrl = playlistUrl;
        this.artistSongCounts = new HashMap<>();
        for (PlaylistInfo info : playlistInfos) {
            Artist artist = info.getArtist();
            int count = info.getCount().intValue();
            artistSongCounts.put(artist, count);
        }
    }

    public List<Map.Entry<Artist, Integer>> getSortedArtistSongCounts() {
        List<Map.Entry<Artist, Integer>> sortedList = new ArrayList<>(artistSongCounts.entrySet());
        sortedList.sort(new Comparator<Map.Entry<Artist, Integer>>() {
            @Override
            public int compare(Map.Entry<Artist, Integer> e1, Map.Entry<Artist, Integer> e2) {
                int countCompare = e2.getValue().compareTo(e1.getValue());
                if (countCompare == 0) {
                    return e1.getKey().getName().compareTo(e2.getKey().getName());
                }
                return countCompare;
            }
        });
        return sortedList;
    }
}