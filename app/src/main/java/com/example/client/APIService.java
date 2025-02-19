package com.example.client;

import com.example.client.pojo.User;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("/user")
    Call<User> getUser();
}
