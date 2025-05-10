package com.example.musementfrontend.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MediaUploadUtil {

    /**
     * Reads an image from Uri, compresses it to JPEG 80% and packs it into MultipartBody.Part.
     *
     * @param ctx      — any Context
     * @param partName — a name of the part in the multipart request (e.g. "file")
     * @param fileUri  — Uri of the image
     */
    public static MultipartBody.Part prepareFilePart(
            Context ctx,
            String partName,
            Uri fileUri
    ) throws IOException {
        // 1) MIME type and extension
        String mimeType = ctx.getContentResolver().getType(fileUri);
        String extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mimeType);
        if (extension == null)
            extension = "jpg";

        // 2) calculates dimensions of the image
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        try (InputStream is = ctx.getContentResolver().openInputStream(fileUri)) {
            BitmapFactory.decodeStream(is, null, opts);
        }

        // 3) sample size so that the image is not larger than 800 px in any dimension
        int maxDim = Math.max(opts.outWidth, opts.outHeight);
        int inSample = 1;
        while (maxDim / inSample > 800) {
            inSample *= 2;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = inSample;

        // 4) decode the image into Bitmap
        Bitmap bitmap;
        try (InputStream is = ctx.getContentResolver().openInputStream(fileUri)) {
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        }

        // 5) iteratively compress the image to JPEG (80% quality, <= 1 MB)
        int quality = 80;
        byte[] imageBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do {
            baos.reset();
            Objects.requireNonNull(bitmap).compress(Bitmap.CompressFormat.JPEG, quality, baos);
            imageBytes = baos.toByteArray();

            // no less than 10% quality
            quality = Math.max(10, quality - 5);

        } while (imageBytes.length > 1_024 * 1_024 && quality > 10);

        // 6) create MultipartBody.Part
        RequestBody reqBody = RequestBody.create(
                imageBytes,
                MediaType.parse(Objects.requireNonNull(mimeType))
        );
        String filename = "upload." + extension;
        return MultipartBody.Part.createFormData(
                partName, filename, reqBody
        );
    }
}
