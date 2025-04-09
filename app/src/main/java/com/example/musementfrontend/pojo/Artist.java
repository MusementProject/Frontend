package com.example.musementfrontend.pojo;

import java.util.Set;

public class Artist {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private Set<Concert> concerts;

    public Artist(int id, String name, String description, String imageUrl, Set<Concert> concerts) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.concerts = concerts;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Set<Concert> getConcerts() {
        return concerts;
    }
}
