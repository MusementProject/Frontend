package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;
import com.example.musementfrontend.util.UtilFeed;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    private UserDTO userDTO;
    private APIService api;
    private TextView name;
    private ImageView avatar;
    private ActivityResultLauncher<Intent> profileSettingsLauncher;
    private String accessToken;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UtilButtons.Init(this);

        profileSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        // Извлеките из data те поля, которые может изменить пользователь
                        String newUsername = data.getStringExtra("username");
                        String newBio      = data.getStringExtra("bio");
                        String newNick     = data.getStringExtra("nickname");
                        String newAvatar   = data.getStringExtra("profilePicture");

                        // Обновите userDTO и UI
                        userDTO.setUsername(newUsername);
                        userDTO.setBio(newBio);
                        userDTO.setNickname(newNick);
                        userDTO.setProfilePicture(newAvatar);

                        name.setText(newUsername);
                        setUserAvatar();
                    }
                }
        );

        // 1) Инициализируем API и вытаскиваем токен + id
        api = APIClient.getClient().create(APIService.class);
        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken");
        userId = intent.getLongExtra("userId", 0L);

        // 2) Подготовим DTO из Intent (для мгновенного UI) — аватарки пока нет
        userDTO = new UserDTO(
                intent.getStringExtra("username"),
                intent.getStringExtra("email"),
                intent.getStringExtra("bio"),
                intent.getStringExtra("nickname"),
                null
        );

        // 3) Найдём вьюхи и сразу выставим имя + дефолтную картинку
        name = findViewById(R.id.name);
        avatar = findViewById(R.id.avatar);
        ImageButton settings = findViewById(R.id.settings);

        name.setText(userDTO.getUsername());
        setUserAvatar();

        // 4) Дёргаем сервер за полными данными текущего пользователя
        api.getCurrentUser("Bearer " + accessToken)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User serverUser = response.body();
                            // Обновляем DTO
                            userDTO.setUsername(serverUser.getUsername());
                            userDTO.setEmail(serverUser.getEmail());
                            userDTO.setBio(serverUser.getBio());
                            userDTO.setNickname(serverUser.getNickname());
                            userDTO.setProfilePicture(serverUser.getProfilePicture());
                            // Обновляем UI
                            name.setText(userDTO.getUsername());
                            setUserAvatar();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "null";
                                Log.e("Profile", "Ошибка загрузки профиля. Code="
                                        + response.code()
                                        + " message=" + response.message()
                                        + " errorBody=" + errorBody);
                            } catch (IOException e) {
                                Log.e("Profile", "Ошибка чтения errorBody", e);
                            }
                            Toast.makeText(Profile.this,
                                    "Не удалось загрузить профиль: code=" + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e("Profile", "Сетевая ошибка при загрузке профиля", t);
                        Toast.makeText(Profile.this,
                                "Ошибка сети: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

        fillUserConcerts();

        // Обработчик меню
        settings.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(this, view);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.profile_settings) {
                    Intent intentSettings = new Intent(Profile.this, ProfileSettings.class);
                    // Передаём поля UserDTO и дополнительные данные
                    intentSettings.putExtra("username", userDTO.getUsername());
                    intentSettings.putExtra("email", userDTO.getEmail());
                    intentSettings.putExtra("bio", userDTO.getBio());
                    intentSettings.putExtra("nickname", userDTO.getNickname());
                    intentSettings.putExtra("profilePicture", userDTO.getProfilePicture());
                    intentSettings.putExtra("accessToken", accessToken);
                    intentSettings.putExtra("userId", userId);
                    profileSettingsLauncher.launch(intentSettings); // Запускаем через лаунчер
                    return true;
                }
                if (itemId == R.id.log_out) {
                    logOut();
                    return true;
                }
                return false;
            });
            menu.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем UI из userDTO
        if (userDTO != null) {
            name.setText(userDTO.getUsername());
            setUserAvatar();
        }
    }

    private void setUserAvatar() {
        String url = userDTO.getProfilePicture();
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .circleCrop()
                    .into(avatar);
        } else {
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .circleCrop()
                    .into(avatar);
        }
    }

    private void logOut() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    finish();
                    Intent intent = new Intent(Profile.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }

    private void fillUserConcerts() {
        List<Concert> concerts = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            concerts.add(new Concert(1, 1, "https://vdnh.ru/upload/resize_cache/iblock/edb/1000_1000_1/edb1fcf17e7b3933296993fac951fd9c.jpg", "A2", new Date(1000)));
        }
        UtilFeed.FillFeedConcert(this, concerts);
    }

    public void OnClickFriends(View view) {
    }

    public void OnClickTickets(View view) {
        Intent intent = new Intent(this, Tickets.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void OnClickPlaylists(View view) {
        Intent intent = new Intent(this, Playlists.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("username", userDTO.getUsername());
        intent.putExtra("email", userDTO.getEmail());
        intent.putExtra("bio", userDTO.getBio());
        intent.putExtra("nickname", userDTO.getNickname());
        intent.putExtra("profilePicture", userDTO.getProfilePicture());
        startActivity(intent);
    }

    public void OnClickSocialNetworks(View view) {
    }

    public void OnClickProfileSettings(View view) {
    }
}