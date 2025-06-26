package com.example.musementfrontend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendConcertDTO {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private boolean isAttending;
    private boolean isWishlisted;
} 