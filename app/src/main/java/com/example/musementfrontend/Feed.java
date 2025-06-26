package com.example.musementfrontend;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.ConcertDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;
import com.example.musementfrontend.util.UtilFeed;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Feed extends AppCompatActivity {
    private View loadingText;
    private boolean isLoading = false;
    private Call<List<ConcertDTO>> currentCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        UtilButtons.Init(this);
        loadConcertFeed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConcertFeed();
    }

    private void showLoading() {
        View feedItem = findViewById(R.id.feed_item);
        if (feedItem != null) {
            feedItem.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        View feedItem = findViewById(R.id.feed_item);
        if (feedItem != null) {
            feedItem.setVisibility(View.VISIBLE);
        }
    }

    private void loadConcertFeed() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        User user = Util.getUser(getIntent());
        if (user == null) {
            isLoading = false;
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_LONG).show();
            return;
        }
        APIService apiService = APIClient.getClient().create(APIService.class);
        currentCall = apiService.getConcertFeed("Bearer " + user.getAccessToken(), user.getId());
        currentCall.enqueue(new Callback<List<ConcertDTO>>() {
            @Override
            public void onResponse(Call<List<ConcertDTO>> call, Response<List<ConcertDTO>> response) {
                isLoading = false;
                currentCall = null;
                if (response.isSuccessful() && response.body() != null) {
                    List<ConcertDTO> concertDTOs = response.body();
                    View feedItem = findViewById(R.id.feed_item);
                    if (feedItem != null) {
                        LinearLayout feed = feedItem.findViewById(R.id.feed);
                        if (feed != null) {
                            feed.removeAllViews();
                            UtilFeed.FillFeedConcert(Feed.this, concertDTOs);
                        } else {
                            Toast.makeText(Feed.this, "Error: feed not found", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Feed.this, "Error: feedItem not found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Feed.this, "Error loading concerts: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ConcertDTO>> call, Throwable t) {
                isLoading = false;
                currentCall = null;
                Toast.makeText(Feed.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}