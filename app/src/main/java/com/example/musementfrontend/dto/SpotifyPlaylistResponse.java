package com.example.musementfrontend.dto;

import com.example.musementfrontend.pojo.PlaylistInfo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyPlaylistResponse {
    private Long playlistId;
    private String playlistUrl;
    private List<PlaylistInfo> playlistInfo;
}

