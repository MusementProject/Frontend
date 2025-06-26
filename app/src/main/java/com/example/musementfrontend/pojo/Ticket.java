package com.example.musementfrontend.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private Long id;
    private Long concertId;
    private String concertArtist;
    private String concertLocation;
    private Date concertDate;
    private String fileUrl;
    private String fileFormat;
    private Date uploadedAt;
}
