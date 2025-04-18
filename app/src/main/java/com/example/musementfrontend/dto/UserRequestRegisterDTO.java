package com.example.musementfrontend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRequestRegisterDTO {
    private String login;
    private String username;
    private String email;
    private String password;
}
