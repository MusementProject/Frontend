package com.example.musementfrontend.pojo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Concert {
    private int id_concert;
    private int id_artist;
    private String imageUrl;
    private String location;
    private Date date;
}
