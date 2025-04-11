package com.example.musementfrontend;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.UtilButtons;
import com.example.musementfrontend.util.UtilFeed;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Feed extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feed);
        UtilButtons.Init(this);
        ConstraintLayout feed = findViewById(R.id.feed_item);
        fillConcerts();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        Log.d("Profile", "menu");
        return true;
    }

    public void fillConcerts(){
        // get content from data_base
        List<Concert> concerts = new ArrayList<>();
        for (int i = 0; i < 20; ++i){
            concerts.add(new Concert(1, 1,  "https://vdnh.ru/upload/resize_cache/iblock/edb/1000_1000_1/edb1fcf17e7b3933296993fac951fd9c.jpg", "A2", new Date(1000)));
        }
        UtilFeed.FillFeedConcert(this, concerts);
    }
}