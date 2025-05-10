package com.example.musementfrontend.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MediaUploadUtil {

    /**
     * Читает картинку из Uri, сжимает её до JPEG 80% и упаковывает в MultipartBody.Part.
     *
     * @param ctx      — любой Context
     * @param partName — имя поля в form-data (у вас "file")
     * @param fileUri  — Uri картинки (галерея или камера)
     */
    public static MultipartBody.Part prepareFilePart(
            Context ctx,
            String partName,
            Uri fileUri
    ) throws IOException {
        // 1) MIME-тип и расширение
        String mimeType = ctx.getContentResolver().getType(fileUri);
        String extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mimeType);
        if (extension == null) extension = "jpg";

        // 2) Считаем размеры, чтобы не держать в памяти гигант
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        try (InputStream is = ctx.getContentResolver().openInputStream(fileUri)) {
            BitmapFactory.decodeStream(is, null, opts);
        }

        // 3) Сэмплируем, чтобы максимальная сторона ≲800px
        int maxDim = Math.max(opts.outWidth, opts.outHeight);
        int inSample = 1;
        while (maxDim / inSample > 800) {
            inSample *= 2;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = inSample;

        // 4) Собираем Bitmap
        Bitmap bitmap;
        try (InputStream is = ctx.getContentResolver().openInputStream(fileUri)) {
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        }

        // 5) Итеративно сжимаем в JPEG, снижая quality, чтобы укладываться в 1 MB
        int quality = 80;
        byte[] imageBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            imageBytes = baos.toByteArray();
            quality = Math.max(10, quality - 5);  // не падаем ниже quality=10
        } while (imageBytes.length > 1_024 * 1_024 && quality > 10);

        // 6) Формируем RequestBody и Multipart
        RequestBody reqBody = RequestBody.create(
                MediaType.parse(mimeType),
                imageBytes
        );
        String filename = "upload." + extension;
        return MultipartBody.Part.createFormData(
                partName, filename, reqBody
        );
    }
}
