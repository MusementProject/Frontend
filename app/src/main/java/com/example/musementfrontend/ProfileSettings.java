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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.util.MediaUploader;
import com.example.musementfrontend.util.UtilButtons;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettings extends AppCompatActivity {
    private ImageView avatarView;
    private ActivityResultLauncher<String> pickLauncher;
    private UserDTO userDTO;
    private APIService api;
    private String accessToken;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_settings);
        UtilButtons.Init(this);
        avatarView = findViewById(R.id.ivAvatar);

        api = APIClient.getClient().create(APIService.class);

        // get data from intent
        Intent intent = getIntent();
        userDTO = new UserDTO(
                intent.getStringExtra("username"),
                intent.getStringExtra("email"),
                intent.getStringExtra("bio"),
                intent.getStringExtra("nickname"),
                intent.getStringExtra("profilePicture")
        );
        accessToken = intent.getStringExtra("accessToken");
        userId = intent.getLongExtra("userId", 0);

        // check if userDTO is null
        if (userDTO.getUsername() == null || accessToken == null || userId == 0) {
            Toast.makeText(this,
                    "The session has expired. Please log in again.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // initialize fields
        setUsername(userDTO.getUsername());
        setNickname(userDTO.getNickname());
        setBio(userDTO.getBio());
        setTelegram(""); // TODO: add telegram field to userDTO
        setAvatar();

        // Launcher for picking an image
        pickLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) uploadNewAvatar(uri);
                }
        );

        // Set up the avatar view and button
        avatarView.setOnClickListener(v -> pickLauncher.launch("image/*"));
        findViewById(R.id.btnChangeAvatar)
                .setOnClickListener(v -> pickLauncher.launch("image/*"));

        // SAVE PROFILE
        Button saveBtn = findViewById(R.id.btnSave);
        saveBtn.setOnClickListener(v -> saveProfile());
    }

    private void uploadNewAvatar(Uri uri) {
        MediaUploader.uploadAvatar(
                this,
                uri,
                "Bearer " + accessToken,
                new MediaUploader.OnResultListener() {
                    @Override
                    public void onSuccess(String url) {

                        // update avatar in UI
                        Glide.with(ProfileSettings.this)
                                .load(url)
                                .circleCrop()
                                .into(avatarView);

                        // update userDTO
                        userDTO.setProfilePicture(url);
                    }

                    @Override
                    public void onError(String err) {
                        Toast.makeText(ProfileSettings.this,
                                "Error uploading avatar: " + err,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void saveProfile() {
        // get data from fields
        String username = ((EditText) findViewById(R.id.etName)).getText().toString().trim();
        String nickname = ((EditText) findViewById(R.id.etNickname)).getText().toString().trim();
        String bio = ((EditText) findViewById(R.id.etBio)).getText().toString().trim();
        String telegram = ((EditText) findViewById(R.id.etTelegram)).getText().toString().trim();

        UserDTO req = new UserDTO(
                username,
                userDTO.getEmail(),
                bio,
                nickname,
                userDTO.getProfilePicture() // use the updated profile picture URL
        );

        api.updateUser(
                        "Bearer " + accessToken,
                        String.valueOf(userId),
                        req
                )
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {

                            // update userDTO with server response
                            User serverUser = resp.body();
                            userDTO.setUsername(serverUser.getUsername());
                            userDTO.setEmail(serverUser.getEmail());
                            userDTO.setBio(serverUser.getBio());
                            userDTO.setNickname(serverUser.getNickname());
                            userDTO.setProfilePicture(serverUser.getProfilePicture());

                            Toast.makeText(ProfileSettings.this,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT).show();

                            // return updated data to the calling activity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("username", userDTO.getUsername());
                            resultIntent.putExtra("email", userDTO.getEmail());
                            resultIntent.putExtra("bio", userDTO.getBio());
                            resultIntent.putExtra("nickname", userDTO.getNickname());
                            resultIntent.putExtra("profilePicture", userDTO.getProfilePicture());
                            setResult(RESULT_OK, resultIntent);
                            finish(); // close the activity
                        } else {
                            Toast.makeText(ProfileSettings.this,
                                    "Error updating profile: " + resp.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(ProfileSettings.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Setters for UI elements
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

    private void setAvatar() {
        String url = userDTO.getProfilePicture();
        if (url != null && !url.isEmpty() && !url.equals("null")) {
            Glide.with(this)
                    .load(url)
                    // TODO: check if disable caching is needed
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.default_avatar)
                    .circleCrop()
                    .into(avatarView);
        } else {
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .circleCrop()
                    .into(avatarView);
        }
    }
}