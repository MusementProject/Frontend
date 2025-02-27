package com.example.musementfrontend.pojo;

public class Concert {
    int id;
    String imageUrl;

    public Concert(int id, String src){
        this.id = id;
        this.imageUrl = src;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
