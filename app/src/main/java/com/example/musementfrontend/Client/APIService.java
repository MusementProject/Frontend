package com.example.musementfrontend.Client;

import com.example.musementfrontend.dto.PlaylistResponseDTO;
import com.example.musementfrontend.dto.SpotifyPlaylistRequest;
import com.example.musementfrontend.dto.SpotifyPlaylistResponse;
import com.example.musementfrontend.dto.UserRequestLoginDTO;
import com.example.musementfrontend.dto.UserRequestLoginWithGoogle;
import com.example.musementfrontend.dto.UserRequestRegisterDTO;
import com.example.musementfrontend.dto.UserResponseLoginDTO;
import com.example.musementfrontend.dto.UserResponseRegisterDTO;

import com.example.musementfrontend.pojo.AttendConcertRequest;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.pojo.PlaylistInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @POST("/api/login")
    Call<UserResponseLoginDTO> userLogin(@Body UserRequestLoginDTO userLogin);

    @POST("/api/register")
    Call<UserResponseRegisterDTO> userRegister(@Body UserRequestRegisterDTO userRegister);

    @POST("/api/login/google")
    Call<UserResponseLoginDTO> userLoginWithGoogle(@Body UserRequestLoginWithGoogle userLoginGoogle);

    @POST("/api/playlists/add")
    Call<PlaylistResponseDTO> addPlaylist(@Header("Authorization") String authHeader, @Body SpotifyPlaylistRequest request);

    @GET("/api/playlists/user/{userId}")
    Call<List<PlaylistResponseDTO>> getUserPlaylists(@Header("Authorization") String authHeader, @Path("userId") Long userId);

    @GET("/api/playlists/{playlistId}/stats")
    Call<PlaylistResponseDTO> getPlaylistStats(@Header("Authorization") String authHeader, @Path("playlistId") Long playlistId);

    @GET("/api/concerts/feed/{userId}")
    Call<List<Concert>> getConcertFeed(@Header("Authorization") String authHeader, @Path("userId") Long userId);

    @GET("/api/concerts/attending/{userId}")
    Call<List<Concert>> getAttendingConcerts(@Header("Authorization") String authHeader, @Path("userId") Long userId);

    @POST("/api/concerts/attend")
    Call<Void> attendConcert(
        @Header("Authorization") String authHeader,
        @Query("userId") Long userId,
        @Query("concertId") Long concertId
    );
}
