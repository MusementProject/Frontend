package com.example.musementfrontend.Client;

import com.example.musementfrontend.dto.SpotifyPlaylistRequest;
import com.example.musementfrontend.dto.SpotifyPlaylistResponse;
import com.example.musementfrontend.dto.UserRequestLoginDTO;
import com.example.musementfrontend.dto.UserRequestLoginWithGoogle;
import com.example.musementfrontend.dto.UserRequestRegisterDTO;
import com.example.musementfrontend.dto.UserResponseLoginDTO;
import com.example.musementfrontend.dto.UserResponseRegisterDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIService {

    @POST("/api/login")
    Call<UserResponseLoginDTO> userLogin(@Body UserRequestLoginDTO userLogin);

    @POST("/api/register")
    Call<UserResponseRegisterDTO> userRegister(@Body UserRequestRegisterDTO userRegister);

    @POST("/api/login/google")
    Call<UserResponseLoginDTO> userLoginWithGoogle(@Body UserRequestLoginWithGoogle userLoginGoogle);

    @POST("/api/playlists/add")
    Call<List<PlaylistInfo>> addPlaylist(@Header("Authorization") String authHeader, @Body SpotifyPlaylistRequest request);
}
