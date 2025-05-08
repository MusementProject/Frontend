package com.example.musementfrontend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.util.MediaUploader;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.util.MediaUploader;
import com.example.musementfrontend.util.Util;
import android.widget.Toast;
import com.example.musementfrontend.util.Util;


public class ProfileSettings extends AppCompatActivity {
    private ImageView avatarView;
    private String bearer;
    private ActivityResultLauncher<String> pickLauncher;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        user = Util.getUser(getIntent());

        // 1) Найти ImageView
        avatarView = findViewById(R.id.ivAvatar);

        // 2) Забрать User и token
        // 2) Извлекаем User сразу из Intent
        user = Util.getUser(getIntent());
        if (user == null) {
            Toast.makeText(this, "Сессия истекла, пожалуйста, зайдите снова", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3) Токен — из самого объекта User
        bearer = "Bearer " + user.getAccessToken();

        // 3) Показать текущий аватар, если есть
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .into(avatarView);
        }

        // 4) Инициализировать лаунчер выбора картинки
        pickLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> { if (uri!=null) uploadNewAvatar(uri); }
        );

        // 5) По клику — открыть галерею
        avatarView.setOnClickListener(v -> pickLauncher.launch("image/*"));
    }

    private void uploadNewAvatar(Uri uri) {
        MediaUploader.uploadAvatar(
                this, uri, bearer,
                new MediaUploader.OnResultListener() {
                    @Override
                    public void onSuccess(String url) {
                        // после успешного аплоада можно обновить и поле в User
                        user.setPhotoUrl(url);
                        Glide.with(ProfileSettings.this)
                                .load(url)
                                .circleCrop()
                                .into(avatarView);
                        Toast.makeText(ProfileSettings.this,
                                "Аватар обновлён!",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String err) {
                        Toast.makeText(ProfileSettings.this,
                                "Ошибка: "+err,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}
