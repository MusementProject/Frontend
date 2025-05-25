package com.example.musementfrontend.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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
    private String fileFormat;   // e.g. "jpg" or "pdf"
    private Date uploadedAt;
}
