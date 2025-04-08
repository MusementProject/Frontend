package com.example.musementfrontend.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.R;
import com.example.musementfrontend.pojo.Concert;

import java.util.List;

public class UtilFeed {
    static public void FillFeedConcert(AppCompatActivity activity, List<Concert> concerts){
        ScrollView scroll = activity.findViewById(R.id.scroll);
        ConstraintLayout layout = scroll.findViewById(R.id.feed_item);
        LinearLayout feed = layout.findViewById(R.id.feed);

        for(Concert concert : concerts){
            View concertView = activity.getLayoutInflater().inflate(R.layout.concert_item, feed, false);
            ImageButton concertImage = concertView.findViewById(R.id.concert);
            Glide.with(activity)
                    .load(concert.getImageUrl())
                    .into(concertImage);
            TextView artist = concertView.findViewById(R.id.artist);
            TextView location = concertView.findViewById(R.id.location);
            TextView date = concertView.findViewById(R.id.date);
            artist.setText("Dima Bilan"); // change!!!
            location.setText(concert.getLocation());
            date.setText(concert.getDate().toString());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) concertView.getLayoutParams();

            params.setMargins(0, 0,0 , 80);
            concertView.setLayoutParams(params);
            feed.addView(concertView);
        }
    }
}
