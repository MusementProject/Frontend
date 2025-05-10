package com.example.musementfrontend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponseDTO {
    private Long id;
    private String url;
    private String createdAt;
}
