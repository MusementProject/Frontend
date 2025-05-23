package com.example.musementfrontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class UserDTO {
        private String username;
        private String email;
        private String bio;
        private String nickname;
        private String profilePicture;
    }
