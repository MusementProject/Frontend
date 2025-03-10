package com.example.musementfrontend.Client;

import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.dto.UserLoginDTO;
import com.example.musementfrontend.dto.UserRegisterDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {

    @POST("/login")
    Call<UserDTO> userLogin(@Body UserLoginDTO userLogin);

    @POST("/register")
    Call<UserDTO> userRegister(@Body UserRegisterDTO userRegister);

}
