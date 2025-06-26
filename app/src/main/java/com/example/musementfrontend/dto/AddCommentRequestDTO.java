package com.example.musementfrontend.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentRequestDTO {
    private Long userId;
    private Long concertId;
    private String message;
    private Date time;
}
