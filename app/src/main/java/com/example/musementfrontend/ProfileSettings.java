package com.example.musementfrontend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;

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
//
//         Button saveButton = findViewById(R.id.btnSave);
//         saveButton.setOnClickListener(v -> {
//
//         });
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
