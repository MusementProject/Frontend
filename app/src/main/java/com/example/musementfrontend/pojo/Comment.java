package com.example.musementfrontend.pojo;

import com.example.musementfrontend.dto.User;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private User user;
    private String message;
    private Date time;
    private String attachedPicture;
    private List<String> tags;
}
