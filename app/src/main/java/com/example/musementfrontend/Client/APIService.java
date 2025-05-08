package com.example.musementfrontend.Client;

import com.example.musementfrontend.dto.ImageResponseDTO;
import com.example.musementfrontend.dto.SpotifyPlaylistRequest;
import com.example.musementfrontend.dto.SpotifyPlaylistResponse;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.dto.UserRequestLoginDTO;
import com.example.musementfrontend.dto.UserRequestLoginWithGoogle;
import com.example.musementfrontend.dto.UserRequestRegisterDTO;
import com.example.musementfrontend.dto.UserResponseLoginDTO;
import com.example.musementfrontend.dto.UserResponseRegisterDTO;
import com.example.musementfrontend.pojo.PlaylistInfo;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface APIService {

    @POST("/api/login")
    Call<UserResponseLoginDTO> userLogin(@Body UserRequestLoginDTO userLogin);

    @POST("/api/register")
    Call<UserResponseRegisterDTO> userRegister(@Body UserRequestRegisterDTO userRegister);

    @POST("/api/login/google")
    Call<UserResponseLoginDTO> userLoginWithGoogle(@Body UserRequestLoginWithGoogle userLoginGoogle);

    @POST("/api/playlists/add")
    Call<List<PlaylistInfo>> addPlaylist(@Header("Authorization") String authHeader, @Body SpotifyPlaylistRequest request);

    @Multipart
    @POST("/api/media/upload")
    Call<ImageResponseDTO> uploadMedia(
            @Header("Authorization") String authHeader,
            @Part MultipartBody.Part file
    );
}
