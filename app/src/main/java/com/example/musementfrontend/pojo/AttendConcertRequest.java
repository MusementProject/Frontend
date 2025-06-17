package com.example.musementfrontend.pojo;

public class AttendConcertRequest {
    private Long userId;
    private Long concertId;

    public AttendConcertRequest(Long userId, Long concertId) {
        this.userId = userId;
        this.concertId = concertId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getConcertId() {
        return concertId;
    }
}