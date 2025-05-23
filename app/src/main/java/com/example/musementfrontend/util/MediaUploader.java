package com.example.musementfrontend.util;

import android.content.Context;
import android.net.Uri;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.ImageResponseDTO;

import okhttp3.MultipartBody;

import retrofit2.*;

import androidx.annotation.NonNull;

import java.io.IOException;

public class MediaUploader {
    public interface OnResultListener {
        void onSuccess(String imageUrl);

        void onError(String errorMessage);
    }

    /**
     * Uploads an image to the server, endpoint: /api/media/upload
     *
     * @param ctx        - any Context
     * @param uri        - Uri of the image
     * @param authHeader - Bearer token
     * @param listener   - callback for success or error
     */
    public static void uploadAvatar(
            Context ctx,
            Uri uri,
            String authHeader,
            OnResultListener listener
    ) {
        MultipartBody.Part part;
        try {
            part = MediaUploadUtil.prepareFilePart(ctx, "file", uri);
        } catch (IOException e) {
            listener.onError("Error preparing file: " + e.getMessage());
            return;
        }

        APIService api = APIClient.getClient().create(APIService.class);
        api.uploadMedia(authHeader, part)
                .enqueue(new Callback<ImageResponseDTO>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ImageResponseDTO> call,
                            @NonNull Response<ImageResponseDTO> resp
                    ) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            listener.onSuccess(resp.body().getUrl());
                        } else {
                            listener.onError("Error from server: " + resp.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ImageResponseDTO> call, @NonNull Throwable t) {
                        listener.onError("Error: " + t.getMessage());
                    }
                });
    }
}
