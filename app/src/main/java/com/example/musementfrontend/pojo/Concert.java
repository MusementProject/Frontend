package com.example.musementfrontend.pojo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Concert {
    private int id;
    private int artistId;
    private String artistName;
    private String imageUrl;
    private String location;
    private String date;
    private boolean attending;
    private boolean wishlisted;

    public Concert() {
    }

    public Concert(int id, int artistId, String artistName, String imageUrl, String location, String date, boolean attending, boolean wishlisted) {
        this.id = id;
        this.artistId = artistId;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.location = location;
        this.date = date;
        this.attending = attending;
        this.wishlisted = wishlisted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormattedDate() {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            return date;
        }
    }

    @Override
    public String toString() {
        // name + location + date
        return artistName + " @ " + location + " — " + getFormattedDate();
    }

    public boolean isAttending() {
        return attending;
    }

    public void setAttending(boolean attending) {
        this.attending = attending;
    }

    public boolean isWishlisted() {
        return wishlisted;
    }

    public void setWishlisted(boolean wishlisted) {
        this.wishlisted = wishlisted;
    }
}