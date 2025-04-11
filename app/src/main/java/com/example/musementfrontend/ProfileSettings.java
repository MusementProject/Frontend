package com.example.musementfrontend;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.util.UtilButtons;

public class ProfileSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_settings);

        UtilButtons.Init(this);

//        setUserAvatar();

        // Button saveButton = findViewById(R.id.saveButton);
        // saveButton.setOnClickListener(v -> {
        //     // Логика сохранения настроек
        // });
    }

    private void setUserAvatar() {
        // get info about user avatar
        ImageView avatar = findViewById(R.id.avatar);
        Glide.with(this)
                .load("https://zefirka.club/uploads/posts/2023-01/1673278260_2-zefirka-club-p-serii-chelovek-na-avu-2.png")
                .circleCrop()
                .into(avatar);
    }

}
