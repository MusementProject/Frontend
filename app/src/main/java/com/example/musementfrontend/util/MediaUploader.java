package com.example.musementfrontend.util;

import android.content.Context;
import android.net.Uri;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.ImageResponseDTO;
import com.example.musementfrontend.util.Util;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.*;
import android.content.Context;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaUploader {
    public static byte[] uriToBytes(Context ctx, Uri uri) {
        try (InputStream in = ctx.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read URI", e);
        }
    }
    public interface OnResultListener {
        void onSuccess(String imageUrl);
        void onError(String errorMessage);
    }

    public static void uploadAvatar(
            Context ctx,
            Uri uri,
            String bearerToken,
            OnResultListener listener) {

        // 1) подготовка MultipartBody.Part
        byte[] data = uriToBytes(ctx, uri);
        String mime = ctx.getContentResolver().getType(uri);
        RequestBody rb = RequestBody.create(
                mime != null ? MediaType.parse(mime)
                        : MediaType.parse("image/*"),
                data
        );
        MultipartBody.Part part = MultipartBody.Part.createFormData(
                "file", "avatar.jpg", rb
        );

        // 2) Получаем APIService из вашего APIClient
        APIService api = APIClient.getClient()
                .create(APIService.class);

        // 3) Делаем вызов
        api.uploadMedia(bearerToken, part)
                .enqueue(new Callback<ImageResponseDTO>() {
                    @Override
                    public void onResponse(
                            Call<ImageResponseDTO> call,
                            Response<ImageResponseDTO> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            listener.onSuccess(resp.body().getUrl());
                        } else {
                            listener.onError("Server error: " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<ImageResponseDTO> call, Throwable t) {
                        listener.onError("Network failure: " + t.getMessage());
                    }
                });
    }
}
