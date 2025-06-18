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
public class PlaylistResponseDTO {
    private Long playlistId;
    private String playlistUrl;
    private String title;
    private List<PlaylistInfo> playlistInfo;
}
