package com.example.musementfrontend.util;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.ConcertComments;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.AttendConcertRequest;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.Util;

import com.example.musementfrontend.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtilFeed {
    private static final String TAG = "UtilFeed";

    public static void FillFeedConcert(AppCompatActivity activity, List<Concert> concerts) {
        ScrollView scroll = activity.findViewById(R.id.scroll);
        View feedItem = activity.findViewById(R.id.feed_item);
        if (feedItem == null) {
            return;
        }
        LinearLayout feed = feedItem.findViewById(R.id.feed);
        if (feed == null) {
            return;
        }
        feed.removeAllViews();
        if (concerts == null || concerts.isEmpty()) {
            return;
        }
        int count = Math.min(concerts.size(), 9);
        for (int i = 0; i < count; ++i) {
            Concert concert = concerts.get(i);
            View concertView = activity.getLayoutInflater().inflate(R.layout.concert_item_for_feed, feed, false);
            if (concertView == null) {
                continue;
            }
            ImageButton concertImage = concertView.findViewById(R.id.concert);
            if (concertImage == null) {
                continue;
            }
            Glide.with(activity)
                    .load(concert.getImageUrl())
                    .into(concertImage);
            TextView artist = concertView.findViewById(R.id.artist);
            TextView location = concertView.findViewById(R.id.location);
            TextView date = concertView.findViewById(R.id.date);
            if (artist == null || location == null || date == null) {
                continue;
            }
            artist.setText(concert.getArtistName());
            location.setText(concert.getLocation());
            date.setText(concert.getFormattedDate());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) concertView.getLayoutParams();
            params.setMargins(0, 40, 0, 40);
            concertView.setLayoutParams(params);
            Button btnGoing = concertView.findViewById(R.id.btn_im_going);
            if (btnGoing == null) {
                continue;
            }
            if (concert.isAttending()) {
                btnGoing.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                btnGoing.setEnabled(false);
            } else {
                btnGoing.setOnClickListener(v -> {
                    btnGoing.setEnabled(false);
                    Toast.makeText(activity, "Will be added to your profile soon!", Toast.LENGTH_SHORT).show();
                    User user = Util.getUser(activity.getIntent());
                    if (user == null) {
                        return;
                    }
                    APIService apiService = APIClient.getClient().create(APIService.class);
                    apiService.attendConcert("Bearer " + user.getAccessToken(), user.getId(), (long) concert.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                btnGoing.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                        }
                    });
                });
            }

            ImageButton comments = concertView.findViewById(R.id.comments);

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ConcertComments.class);
                    intent.putExtra(IntentKeys.getCONCERT_ID(), concert.getId());
                    intent.putExtra(IntentKeys.getUSER(), Util.getUser(activity.getIntent()));
                    activity.startActivity(intent);
                }
            });
            feed.addView(concertView);
        }
    }

    public static void FillProfileConcerts(AppCompatActivity activity, List<Concert> concerts) {
        ScrollView scroll = activity.findViewById(R.id.scroll);
        ConstraintLayout layout = scroll.findViewById(R.id.feed_item);
        LinearLayout feed = layout.findViewById(R.id.feed);

        feed.removeAllViews();

        if (concerts == null || concerts.isEmpty()) {
            return;
        }

        for (Concert concert : concerts) {
            View concertView = activity.getLayoutInflater().inflate(R.layout.concert_item, feed, false);
            ImageButton concertImage = concertView.findViewById(R.id.concert);
            Glide.with(activity)
                    .load(concert.getImageUrl())
                    .into(concertImage);
            TextView artist = concertView.findViewById(R.id.artist);
            TextView location = concertView.findViewById(R.id.location);
            TextView date = concertView.findViewById(R.id.date);

            artist.setText(concert.getArtistName());
            location.setText(concert.getLocation());
            date.setText(concert.getFormattedDate());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) concertView.getLayoutParams();
            params.setMargins(0, 40, 0, 40);
            concertView.setLayoutParams(params);

            ImageButton comments = concertView.findViewById(R.id.comments);

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ConcertComments.class);
                    intent.putExtra(IntentKeys.getCONCERT_ID(), concert.getId());
                    intent.putExtra(IntentKeys.getUSER(), Util.getUser(activity.getIntent()));
                    Log.d("concert id", String.valueOf(concert.getId()));
                    activity.startActivity(intent);
                }
            });

            feed.addView(concertView);
        }
    }

    
}