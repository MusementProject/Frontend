package com.example.musementfrontend.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistInfo {
    private Long userId;
    private Long artistId;
    private Artist artist;
    private Long count;
}
