package com.example.musementfrontend.Client;

import com.example.musementfrontend.dto.FriendDTO;
import com.example.musementfrontend.dto.ImageResponseDTO;
import com.example.musementfrontend.dto.SpotifyPlaylistRequest;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.dto.UserRequestLoginDTO;
import com.example.musementfrontend.dto.UserRequestLoginWithGoogle;
import com.example.musementfrontend.dto.UserRequestRegisterDTO;
import com.example.musementfrontend.dto.UserResponseLoginDTO;
import com.example.musementfrontend.dto.UserResponseRegisterDTO;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.pojo.PlaylistInfo;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {

    @POST("/api/login")
    Call<UserResponseLoginDTO> userLogin(@Body UserRequestLoginDTO userLogin);

    @POST("/api/register")
    Call<UserResponseRegisterDTO> userRegister(@Body UserRequestRegisterDTO userRegister);

    @POST("/api/login/google")
    Call<UserResponseLoginDTO> userLoginWithGoogle(@Body UserRequestLoginWithGoogle userLoginGoogle);

    @POST("/api/playlists/add")
    Call<List<PlaylistInfo>> addPlaylist(@Header("Authorization") String authHeader, @Body SpotifyPlaylistRequest request);

    @PATCH("/api/users/{id}")
    Call<UserDTO> updateUser(@Header("Authorization") String authHeader, @Path("id") long id, @Body UserDTO request);

    @GET("/api/concerts/attend_user/{userId}")
    Call<List<Concert>> getUserConcerts(@Header("Authorization") String authHeader, long id);

    @GET("/api/friends/getAll/{userId}")
    Call<List<FriendDTO>> getAllUserFriends(@Header("Authorization") String authHeader, @Path("userId") long id);

    @GET("/api/friends/getFollowing/{userId}")
    Call<List<FriendDTO>> getAllUserFollowing(@Header("Authorization") String authHeader, @Path("userId") long id);

    @GET("/api/friends/getFollowers/{userId}")
    Call<List<FriendDTO>> getAllUserFollowers(@Header("Authorization") String authHeader, @Path("userId") long id);

    @PATCH("/api/users/{id}")
    Call<User> updateUser(
            @Header("Authorization") String auth,
            @Path("id") String id,
            @Body UserDTO dto
    );

    @Multipart
    @POST("/api/media/upload")
    Call<ImageResponseDTO> uploadMedia(
            @Header("Authorization") String authHeader,
            @Part MultipartBody.Part file
    );

    @GET("/api/users/me")
    Call<User> getCurrentUser(@Header("Authorization") String auth);
}
