package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.UtilButtons;
import com.example.musementfrontend.util.UtilFeed;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    private UserDTO userDTO;
    private APIService api;
    private TextView username;
    private TextView nickname;
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

                        // fields that can be changed by the user
                        String newUsername = data.getStringExtra("username");
                        String newBio = data.getStringExtra("bio");
                        String newNick = data.getStringExtra("nickname");
                        String newAvatar = data.getStringExtra("profilePicture");

                        // update userDTO
                        userDTO.setUsername(newUsername);
                        userDTO.setBio(newBio);
                        userDTO.setNickname(newNick);
                        userDTO.setProfilePicture(newAvatar);

                        // update UI
                        username.setText(
                                getString(R.string.username_handle, userDTO.getUsername())
                        );
                        nickname.setText(newNick);
                        setUserAvatar();
                    }
                }
        );

        // 1) init API + get token + userId
        api = APIClient.getClient().create(APIService.class);
        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken");
        userId = intent.getLongExtra("userId", 0L);

        // 2) prepare UserDTO from intent
        // TODO profilePicture ??
        userDTO = new UserDTO(
                intent.getStringExtra("username"),
                intent.getStringExtra("email"),
                intent.getStringExtra("bio"),
                intent.getStringExtra("nickname"),
                null
        );

        // 3) find views and set data
        username = findViewById(R.id.username);
        nickname = findViewById(R.id.nickname);
        avatar = findViewById(R.id.avatar);
        ImageButton settings = findViewById(R.id.settings);

        username.setText(
                getString(R.string.username_handle, userDTO.getUsername())
        );
        nickname.setText(userDTO.getNickname());
        setUserAvatar();

        // 4) load fresh user data from server
        api.getCurrentUser("Bearer " + accessToken)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User serverUser = response.body();

                            // update userDTO from server
                            userDTO.setUsername(serverUser.getUsername());
                            userDTO.setEmail(serverUser.getEmail());
                            userDTO.setBio(serverUser.getBio());
                            userDTO.setNickname(serverUser.getNickname());
                            userDTO.setProfilePicture(serverUser.getProfilePicture());

                            // update UI
                            username.setText(
                                    getString(R.string.username_handle, userDTO.getUsername())
                            );
                            nickname.setText(userDTO.getNickname());
                            setUserAvatar();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "null";
                                Log.e("Profile", "Error loading user data: code="
                                        + response.code()
                                        + " message=" + response.message()
                                        + " errorBody=" + errorBody);
                            } catch (IOException e) {
                                Log.e("Profile", "Error reading error body", e);
                            }
                            Toast.makeText(Profile.this,
                                    "Error loading profile: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.e("Profile", "Error during network request", t);
                        Toast.makeText(Profile.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

        fillUserConcerts();

        // set up menu buttons
        settings.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(this, view);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.profile_settings) {
                    Intent intentSettings = getIntentSettings();

                    // start ProfileSettings activity with launcher
                    profileSettingsLauncher.launch(intentSettings);
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

    @NonNull
    private Intent getIntentSettings() {
        Intent intentSettings = new Intent(Profile.this, ProfileSettings.class);

        // put all user data into intent
        intentSettings.putExtra("username", userDTO.getUsername());
        intentSettings.putExtra("email", userDTO.getEmail());
        intentSettings.putExtra("bio", userDTO.getBio());
        intentSettings.putExtra("nickname", userDTO.getNickname());
        intentSettings.putExtra("profilePicture", userDTO.getProfilePicture());
        intentSettings.putExtra("accessToken", accessToken);
        intentSettings.putExtra("userId", userId);
        return intentSettings;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // update UI with user data
        if (userDTO != null) {
            username.setText(
                    getString(R.string.username_handle, userDTO.getUsername())
            );
            nickname.setText(userDTO.getNickname());
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
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                finish();
                Intent intent = new Intent(Profile.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }

    private void fillUserConcerts() {
        List<Concert> concerts = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            concerts.add(new Concert(
                    1,
                    1,
                    "https://vdnh.ru/upload/resize_cache/iblock/edb/1000_1000_1/edb1fcf17e7b3933296993fac951fd9c.jpg",
                    "A2",
                    new Date(1000)));
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