package com.example.musementfrontend;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        Util.Init(this);
        setUserAvatar();

        fillUserConcerts();
    }

    private void setUserAvatar(){
        // get info about user avatar
        ImageView avatar = findViewById(R.id.avatar);
        Glide.with(this)
                .load("https://zefirka.club/uploads/posts/2023-01/1673278260_2-zefirka-club-p-serii-chelovek-na-avu-2.png")
                .circleCrop()
                .into(avatar);
    }

    private void fillUserConcerts(){
        // get user concerts from database!!
        List<Concert> concerts = new ArrayList<>();
        for (int i = 0; i < 20; ++i){
            concerts.add(new Concert(1,1,  "https://vdnh.ru/upload/resize_cache/iblock/edb/1000_1000_1/edb1fcf17e7b3933296993fac951fd9c.jpg", "A2"));
        }
        Util.FillFeedConcert(this, concerts);
    }
}