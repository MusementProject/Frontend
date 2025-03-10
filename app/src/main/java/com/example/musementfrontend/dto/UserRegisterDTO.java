package com.example.musementfrontend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRegisterDTO {
    String login;
    String username;
    String email;
    String password;
}
