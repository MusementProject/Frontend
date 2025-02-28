package com.example.musementfrontend.pojo;

import java.sql.Date;

public class Concert {
    int id_concert;
    int id_artist;
    String imageUrl;
    String location;
    Date date;



    public Concert(int id_concert, int id_artist, String imageUrl , String location){
        this.id_concert = id_concert;
        this.id_artist = id_artist;
        this.imageUrl = imageUrl;
        this.location = location;
        this.date = new Date(1000000);
    }

    public int getId_concert() {
        return id_concert;
    }

    public int getId_artist() {
        return id_artist;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }
}
