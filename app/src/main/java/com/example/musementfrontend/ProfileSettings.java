package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.dto.UserUpdateDTO;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_settings);

        UtilButtons.Init(this);

        User user = Util.getUser(getIntent());
        if (user != null) {
            setUsername(user.getUsername());
            setNickname(user.getNickname());
        }
        setUserAvatar();

         Button saveButton = findViewById(R.id.btnSave);
         saveButton.setOnClickListener(v -> {
             EditText username = findViewById(R.id.etName);
             EditText nickname = findViewById(R.id.etNickname);
             EditText bio = findViewById(R.id.etBio);
             EditText telegram = findViewById(R.id.etTelegram);
             UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
             userUpdateDTO.setUsername(username.getText().toString());
             userUpdateDTO.setNickname(nickname.getText().toString());
             userUpdateDTO.setBio(bio.getText().toString());
             userUpdateDTO.setTelegram(telegram.getText().toString());
             APIService apiService = APIClient.getClient().create(APIService.class);
             Call<UserUpdateDTO> call = apiService.updateUser("Bearer " + user.getAccessToken(), user.getId(), userUpdateDTO);
             call.enqueue(new Callback<UserUpdateDTO>() {
                 @Override
                 public void onResponse(Call<UserUpdateDTO> call, Response<UserUpdateDTO> response) {
                     if (response.isSuccessful() && response.body() != null){
                         UserUpdateDTO userUpdate = response.body();
                         user.setUsername(userUpdate.getUsername());
                         user.setNickname(userUpdate.getNickname());
                         user.setBio(userUpdate.getBio());
                         user.setTelegram(userUpdate.getTelegram());
                         Intent intent = new Intent(ProfileSettings.this, Profile.class);
                         intent.putExtra(IntentKeys.getUSER_KEY(), user);
                         startActivity(intent);
                         finish();
                     }
                 }

                 @Override
                 public void onFailure(Call<UserUpdateDTO> call, Throwable t) {

                 }
             });
         });
    }

    private void setUserAvatar() {
        // get info about user avatar
        ImageView avatar = findViewById(R.id.ivAvatar);
        Glide.with(this)
                .load("https://zefirka.club/uploads/posts/2023-01/1673278260_2-zefirka-club-p-serii-chelovek-na-avu-2.png")
                .circleCrop()
                .into(avatar);
    }

    private void setUsername(String currentUsername) {
        EditText username = findViewById(R.id.etName);
        username.setText(currentUsername);
    }

    private void setNickname(String currentNickname) {
        if (currentNickname == null){
            return;
        }
        EditText nickname = findViewById(R.id.etNickname);
        nickname.setText(currentNickname);
    }

}
