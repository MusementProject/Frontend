package com.example.musementfrontend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConcertDTO {
    private Long id;
    private Long artistId;
    private String artistName;
    private String imageUrl;
    private String location;
    private String date;

    @Override
    public String toString() {
        String d = date != null && date.contains("T")
                ? date.split("T")[0]
                : date;
        return artistName + " @ " + location + " — " + d;
    }
}
