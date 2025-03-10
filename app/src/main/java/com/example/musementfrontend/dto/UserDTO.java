package com.example.musementfrontend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String email;
    private String bio;
    private String fullName;
    private String profilePicture;
}
