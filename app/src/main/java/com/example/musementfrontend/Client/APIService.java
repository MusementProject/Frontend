package com.example.musementfrontend.Client;

import com.example.musementfrontend.dto.PlaylistResponseDTO;
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

import com.example.musementfrontend.pojo.AttendConcertRequest;
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
import retrofit2.http.Query;
import retrofit2.http.Part;

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

    @PATCH("/api/users/id/{id}")
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

    @GET("/api/users/username/{username}")
    Call<User> getUserByUsername(@Header("Authorization") String authHeader, @Path("username") String username);

    @GET("/api/friends/get/{userId}/{friendId}")
    Call<Boolean> getFriendshipInfo(@Header("Authorization") String authHeader, @Path("userId") Long userId, @Path("friendId") Long friendId);

    @POST("/api/friends/add/{userId}/{friendId}")
    Call<FriendDTO> addFriend(@Header("Authorization") String authHeader, @Path("userId") Long userId, @Path("friendId") Long friendId);

    @DELETE("/api/friends/delete/{userId}/{friendId}")
    Call<Boolean> deleteFriend(@Header("Authorization") String authHeader, @Path("userId") Long userId, @Path("friendId") Long friendId);

    @GET("/api/users/searchByUsername")
    Call<List<FriendDTO>> searchByUsername(@Header("Authorization") String authHeader, @Query("q") String query);
}
