package com.example.musementfrontend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.dto.UserUpdateDTO;
import com.example.musementfrontend.util.MediaUploader;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettings extends AppCompatActivity {
    private ImageView avatarView;
    private ActivityResultLauncher<String> pickLauncher;
    private User user;
    private APIService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_settings);
        UtilButtons.Init(this);

        api = APIClient.getClient().create(APIService.class);

        // 1) Получаем User из Intent
        user = Util.getUser(getIntent());
        if (user == null) {
            Toast.makeText(this, "Сессия истекла, зайдите заново", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) Инициализируем поля
        setUsername(user.getUsername());
        setNickname(user.getNickname());
        setBio(user.getBio());
        setTelegram(user.getTelegram());

        // 3) AVATAR
        avatarView = findViewById(R.id.ivAvatar);
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .circleCrop()
                    .into(avatarView);
        }

        // Лаунчер для выбора картинки
        pickLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) uploadNewAvatar(uri);
                }
        );

        // Клик по картинке и кнопке
        avatarView.setOnClickListener(v -> pickLauncher.launch("image/*"));
        findViewById(R.id.btnChangeAvatar)
                .setOnClickListener(v -> pickLauncher.launch("image/*"));

        // 4) SAVE PROFILE
        Button saveBtn = findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(v -> saveProfile());
    }

    private void uploadNewAvatar(Uri uri) {
        MediaUploader.uploadAvatar(
                this, uri, "Bearer " + user.getAccessToken(),
                new MediaUploader.OnResultListener() {
                    @Override
                    public void onSuccess(String url) {
                        // обновляем UI
                        Glide.with(ProfileSettings.this)
                                .load(url)
                                .circleCrop()
                                .into(avatarView);
                        // сохраняем в user
                        user.setPhotoUrl(url);
                    }

                    @Override
                    public void onError(String err) {
                        Toast.makeText(ProfileSettings.this,
                                "Ошибка загрузки: " + err,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void saveProfile() {
        // собираем данные из полей
        String username = ((EditText) findViewById(R.id.etName)).getText().toString().trim();
        String nickname = ((EditText) findViewById(R.id.etNickname)).getText().toString().trim();
        String bio = ((EditText) findViewById(R.id.etBio)).getText().toString().trim();
        String telegram = ((EditText) findViewById(R.id.etTelegram)).getText().toString().trim();
        String fullName = ((EditText) findViewById(R.id.etNickname)).getText().toString().trim();

        UserUpdateDTO req = new UserUpdateDTO(
                username,
                user.getEmail(),
                bio,
                fullName,
                user.getPhotoUrl()   // URL выставленный после uploadNewAvatar
        );

        api.updateUser(
                        "Bearer " + user.getAccessToken(),
                        String.valueOf(user.getId()),
                        req
                )
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            user = resp.body();
                            Toast.makeText(ProfileSettings.this,
                                    "Профиль сохранён", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileSettings.this,
                                    "Ошибка сохранения: " + resp.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(ProfileSettings.this,
                                "Сеть: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // вспомогательные методы для установки текстов
    private void setUsername(String name) {
        EditText et = findViewById(R.id.etName);
        et.setText(name == null ? "" : name);
    }

    private void setNickname(String nick) {
        EditText et = findViewById(R.id.etNickname);
        et.setText(nick == null ? "" : nick);
    }

    private void setBio(String bio) {
        EditText et = findViewById(R.id.etBio);
        et.setText(bio == null ? "" : bio);
    }

    private void setTelegram(String tg) {
        EditText et = findViewById(R.id.etTelegram);
        et.setText(tg == null ? "" : tg);
    }
}
