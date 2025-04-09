package com.example.musementfrontend.pojo;

public class Playlist {
    private int id;
    private String title;
    private String playlistUrl;

    public Playlist(int id, String title, String playlistUrl){
        this.id = id;
        this.title = title;
        this.playlistUrl = playlistUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }
}
