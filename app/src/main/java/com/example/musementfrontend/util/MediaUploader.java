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
    public interface OnResultListener {
        void onSuccess(String imageUrl);
        void onError(String errorMessage);
    }

    /**
     * Загружает аватарку на эндпоинт /api/media/upload
     *
     * @param ctx        любой Context
     * @param uri        Uri картинки
     * @param authHeader токен в формате "Bearer …"
     * @param listener   коллбэк успех/ошибка
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
            listener.onError("Не удалось прочитать изображение");
            return;
        }

        APIService api = APIClient.getClient().create(APIService.class);
        api.uploadMedia(authHeader, part)
                .enqueue(new Callback<ImageResponseDTO>() {
                    @Override
                    public void onResponse(
                            Call<ImageResponseDTO> call,
                            Response<ImageResponseDTO> resp
                    ) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            // допустим, DTO содержит getUrl()
                            listener.onSuccess(resp.body().getUrl());
                        } else {
                            listener.onError("Сервер вернул " + resp.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<ImageResponseDTO> call, Throwable t) {
                        listener.onError("Сеть: " + t.getMessage());
                    }
                });
    }
}
