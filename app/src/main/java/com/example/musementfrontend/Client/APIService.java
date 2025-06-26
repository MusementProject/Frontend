package com.example.musementfrontend.Client;

import com.example.musementfrontend.dto.PlaylistResponseDTO;
import com.example.musementfrontend.dto.FriendDTO;
import com.example.musementfrontend.dto.ImageResponseDTO;
import com.example.musementfrontend.dto.PlaylistResponseDTO;
import com.example.musementfrontend.dto.SpotifyPlaylistRequest;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.dto.UserRequestLoginDTO;
import com.example.musementfrontend.dto.UserRequestLoginWithGoogle;
import com.example.musementfrontend.dto.UserRequestRegisterDTO;
import com.example.musementfrontend.dto.UserResponseLoginDTO;
import com.example.musementfrontend.dto.UserResponseRegisterDTO;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.pojo.Ticket;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    Call<List<ConcertDTO>> getConcertFeed(@Header("Authorization") String authHeader, @Path("userId") Long userId);

    @GET("/api/concerts/attending/{userId}")
    Call<List<ConcertDTO>> getAttendingConcerts(@Header("Authorization") String authHeader, @Path("userId") Long userId);

    @POST("/api/concerts/attend")
    Call<Void> attendConcert(
            @Header("Authorization") String authHeader,
            @Query("userId") Long userId,
            @Query("concertId") Long concertId
    );

    @POST("/api/concerts/wishlist")
    Call<Void> addToWishlist(
            @Header("Authorization") String authHeader,
            @Query("userId") Long userId,
            @Query("concertId") Long concertId
    );

    @DELETE("/api/concerts/wishlist")
    Call<Void> removeFromWishlist(
            @Header("Authorization") String authHeader,
            @Query("userId") Long userId,
            @Query("concertId") Long concertId
    );

    @GET("/api/concerts/wishlist/{userId}")
    Call<List<ConcertDTO>> getWishlistConcerts(@Header("Authorization") String authHeader, @Path("userId") Long userId);

    @GET("/api/concerts/{concertId}/wishlist_user/{userId}")
    Call<Boolean> isUserWishlistingConcert(@Header("Authorization") String authHeader, @Path("concertId") Long concertId, @Path("userId") Long userId);

    @POST("/api/concerts/wishlist_to_attending")
    Call<Void> moveFromWishlistToAttending(
            @Header("Authorization") String authHeader,
            @Query("userId") Long userId,
            @Query("concertId") Long concertId
    );

    @PATCH("/api/users/{id}")
    Call<UserDTO> updateUser(@Header("Authorization") String authHeader, @Path("id") long id, @Body UserDTO request);


    @GET("/api/concerts/attending/{userId}")
    Call<List<ConcertDTO>> getUserConcerts(
            @Header("Authorization") String auth,
            @Path("userId") long userId
    );


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

    @GET("api/comment/getAll/{userId}/{concertId}")
    Call<List<Comment>> getConcertComments(@Header("Authorization") String authHeader, @Path("userId") Long userId, @Path("concertId") Long concertId);

    @POST("api/comment/add")
    Call<Comment> addComment(@Header("Authorization") String authHeader, @Body AddCommentRequestDTO commentRequest);
}

@GET("/api/tickets")
Call<List<Ticket>> getTickets(@Header("Authorization") String authHeader);

@Multipart
@POST("/api/tickets")
Call<Ticket> uploadTicket(
        @Header("Authorization") String authHeader,
        @Part("concertId") RequestBody concertId,
        @Part MultipartBody.Part file
);

@Multipart
@PUT("/api/tickets/{id}")
Call<Ticket> replaceTicket(
        @Header("Authorization") String authHeader,
        @Path("id") long ticketId,
        @Part MultipartBody.Part file
);

@DELETE("/api/tickets/{id}")
Call<Void> deleteTicket(
        @Header("Authorization") String authHeader,
        @Path("id") long ticketId
);
