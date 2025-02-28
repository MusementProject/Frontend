package com.example.musementfrontend;

import android.os.Bundle;
import android.widget.ImageButton;
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

public class Feed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feed);
        Util.Init(this);

        ConstraintLayout feed = findViewById(R.id.feed_item);

        fiilConcerts();
    }

    public void fiilConcerts(){
        // get content from data_base
        List<Concert> concerts = new ArrayList<>();
        for (int i = 0; i < 20; ++i){
            concerts.add(new Concert(1,1,  "https://vdnh.ru/upload/resize_cache/iblock/edb/1000_1000_1/edb1fcf17e7b3933296993fac951fd9c.jpg", "A2"));
        }
        Util.FillFeedConcert(this, concerts);
    }
}