package com.example.musementfrontend.util;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.musementfrontend.Profile;
import com.example.musementfrontend.dto.ConcertDTO;
import com.example.musementfrontend.dto.FriendConcertDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.AttendConcertRequest;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.Util;

import com.example.musementfrontend.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            Button btnWantToGo = concertView.findViewById(R.id.btn_want_to_go);
            Button btnFriends = concertView.findViewById(R.id.btn_friends);
            if (btnGoing == null || btnWantToGo == null || btnFriends == null) {
                continue;
            }
            
            if (concert.isAttending()) {
                btnGoing.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                btnGoing.setEnabled(false);
                btnWantToGo.setEnabled(false);
            } else if (concert.isWishlisted()) {
                btnWantToGo.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                btnWantToGo.setEnabled(false);
                btnGoing.setEnabled(false);
            } else {
                btnGoing.setOnClickListener(v -> {
                    btnGoing.setEnabled(false);
                    btnWantToGo.setEnabled(false);
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
                
                btnWantToGo.setOnClickListener(v -> {
                    btnWantToGo.setEnabled(false);
                    btnGoing.setEnabled(false);
                    Toast.makeText(activity, "Added to wishlist!", Toast.LENGTH_SHORT).show();
                    User user = Util.getUser(activity.getIntent());
                    if (user == null) {
                        return;
                    }
                    APIService apiService = APIClient.getClient().create(APIService.class);
                    apiService.addToWishlist("Bearer " + user.getAccessToken(), user.getId(), (long) concert.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                btnWantToGo.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                        }
                    });
                });
            }
            
            btnFriends.setOnClickListener(v -> {
                showFriendsDialog(activity, concert);
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

            feed.addView(concertView);
        }
    }

    public static void FillWishlistConcerts(AppCompatActivity activity, List<Concert> concerts) {
        ScrollView scroll = activity.findViewById(R.id.scroll);
        ConstraintLayout layout = scroll.findViewById(R.id.feed_item);
        LinearLayout feed = layout.findViewById(R.id.feed);

        feed.removeAllViews();

        if (concerts == null || concerts.isEmpty()) {
            return;
        }

        for (Concert concert : concerts) {
            View concertView = activity.getLayoutInflater().inflate(R.layout.concert_item_for_feed, feed, false);
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

            Button btnGoing = concertView.findViewById(R.id.btn_im_going);
            Button btnWantToGo = concertView.findViewById(R.id.btn_want_to_go);
            Button btnFriends = concertView.findViewById(R.id.btn_friends);
            if (btnGoing != null && btnWantToGo != null && btnFriends != null) {
                btnWantToGo.setVisibility(View.GONE);
                btnGoing.setText("I'm going!");
                btnGoing.setOnClickListener(v -> {
                    btnGoing.setEnabled(false);
                    Toast.makeText(activity, "Moving to attending!", Toast.LENGTH_SHORT).show();
                    User user = Util.getUser(activity.getIntent());
                    if (user == null) {
                        return;
                    }
                    APIService apiService = APIClient.getClient().create(APIService.class);
                    apiService.moveFromWishlistToAttending("Bearer " + user.getAccessToken(), user.getId(), (long) concert.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                btnGoing.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                btnGoing.setText("Added!");
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                        }
                    });
                });
                
                btnFriends.setOnClickListener(v -> {
                    showFriendsDialog(activity, concert);
                });
            }

            feed.addView(concertView);
        }
    }

    public static void FillProfileConcertsForFragment(AppCompatActivity activity, List<Concert> concerts, LinearLayout feed) {
        if (feed == null) {
            return;
        }

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

            feed.addView(concertView);
        }
    }

    public static void FillWishlistConcertsForFragment(AppCompatActivity activity, List<Concert> concerts, LinearLayout feed) {
        if (feed == null) {
            return;
        }

        feed.removeAllViews();

        if (concerts == null || concerts.isEmpty()) {
            return;
        }

        for (Concert concert : concerts) {
            View concertView = activity.getLayoutInflater().inflate(R.layout.concert_item_profile, feed, false);
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

            Button btnGoing = concertView.findViewById(R.id.btn_im_going);
            Button btnWantToGo = concertView.findViewById(R.id.btn_want_to_go);
            Button btnFriends = concertView.findViewById(R.id.btn_friends);
            if (btnGoing != null && btnWantToGo != null && btnFriends != null) {
                btnWantToGo.setVisibility(View.GONE);
                btnGoing.setText("I'm going!");
                btnGoing.setOnClickListener(v -> {
                    btnGoing.setEnabled(false);
                    Toast.makeText(activity, "Moving to attending!", Toast.LENGTH_SHORT).show();
                    
                    if (activity instanceof Profile) {
                        Profile profile = (Profile) activity;
                        String accessToken = profile.getIntent().getStringExtra("accessToken");
                        long userId = profile.getIntent().getLongExtra("userId", 0L);
                        
                        if (accessToken == null || userId == 0) {
                            return;
                        }
                        
                        APIService apiService = APIClient.getClient().create(APIService.class);
                        apiService.moveFromWishlistToAttending("Bearer " + accessToken, userId, (long) concert.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    btnGoing.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                    btnGoing.setText("Added!");
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                            }
                        });
                    }
                });
                
                btnFriends.setOnClickListener(v -> {
                    showFriendsDialog(activity, concert);
                });
            }

            feed.addView(concertView);
        }
    }

    public static void FillAttendingConcertsForFragment(AppCompatActivity activity, List<Concert> concerts, LinearLayout feed) {
        if (feed == null) {
            return;
        }

        feed.removeAllViews();

        if (concerts == null || concerts.isEmpty()) {
            return;
        }

        for (Concert concert : concerts) {
            View concertView = activity.getLayoutInflater().inflate(R.layout.concert_item_profile, feed, false);
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

            Button btnGoing = concertView.findViewById(R.id.btn_im_going);
            Button btnWantToGo = concertView.findViewById(R.id.btn_want_to_go);
            if (btnGoing != null && btnWantToGo != null) {
                btnGoing.setVisibility(View.GONE);
                btnWantToGo.setVisibility(View.GONE);
            }

            feed.addView(concertView);
        }
    }

    public static List<Concert> convertConcertDTOsToConcerts(List<ConcertDTO> concertDTOs) {
        List<Concert> concerts = new ArrayList<>();
        for (ConcertDTO dto : concertDTOs) {
            Concert concert = new Concert(
                dto.getId().intValue(),
                dto.getArtistId().intValue(),
                dto.getArtistName(),
                dto.getImageUrl(),
                dto.getLocation(),
                dto.getDate(),
                dto.isAttending(),
                dto.isWishlisted()
            );
            concerts.add(concert);
        }
        return concerts;
    }

    private static void showFriendsDialog(AppCompatActivity activity, Concert concert) {
        User user = Util.getUser(activity.getIntent());
        if (user == null) {
            Toast.makeText(activity, "Error: User not found", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_friends_on_concert, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        APIService apiService = APIClient.getClient().create(APIService.class);
        apiService.getFriendsOnConcert("Bearer " + user.getAccessToken(), (long) concert.getId(), user.getId())
                .enqueue(new Callback<List<FriendConcertDTO>>() {
                    @Override
                    public void onResponse(Call<List<FriendConcertDTO>> call, Response<List<FriendConcertDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            populateFriendsDialog(dialogView, response.body());
                        } else {
                            Toast.makeText(activity, "Error loading friends: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FriendConcertDTO>> call, Throwable t) {
                        Toast.makeText(activity, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void populateFriendsDialog(View dialogView, List<FriendConcertDTO> friends) {
        LinearLayout attendingContainer = dialogView.findViewById(R.id.attending_friends_container);
        LinearLayout wishlistedContainer = dialogView.findViewById(R.id.wishlisted_friends_container);
        TextView tvAttending = dialogView.findViewById(R.id.tv_attending);
        TextView tvWishlisted = dialogView.findViewById(R.id.tv_wishlisted);

        attendingContainer.removeAllViews();
        wishlistedContainer.removeAllViews();

        List<FriendConcertDTO> attendingFriends = new ArrayList<>();
        List<FriendConcertDTO> wishlistedFriends = new ArrayList<>();

        for (FriendConcertDTO friend : friends) {
            if (friend.isAttending()) {
                attendingFriends.add(friend);
            } else if (friend.isWishlisted()) {
                wishlistedFriends.add(friend);
            }
        }

        if (attendingFriends.isEmpty()) {
            tvAttending.setVisibility(View.GONE);
            attendingContainer.setVisibility(View.GONE);
        } else {
            tvAttending.setVisibility(View.VISIBLE);
            attendingContainer.setVisibility(View.VISIBLE);
            for (FriendConcertDTO friend : attendingFriends) {
                View friendView = createFriendView(dialogView.getContext(), friend, true);
                attendingContainer.addView(friendView);
            }
        }

        if (wishlistedFriends.isEmpty()) {
            tvWishlisted.setVisibility(View.GONE);
            wishlistedContainer.setVisibility(View.GONE);
        } else {
            tvWishlisted.setVisibility(View.VISIBLE);
            wishlistedContainer.setVisibility(View.VISIBLE);
            for (FriendConcertDTO friend : wishlistedFriends) {
                View friendView = createFriendView(dialogView.getContext(), friend, false);
                wishlistedContainer.addView(friendView);
            }
        }

        if (attendingFriends.isEmpty() && wishlistedFriends.isEmpty()) {
            TextView noFriendsText = new TextView(dialogView.getContext());
            noFriendsText.setText("No friends on this concert yet");
            noFriendsText.setGravity(android.view.Gravity.CENTER);
            noFriendsText.setPadding(0, 32, 0, 32);
            attendingContainer.addView(noFriendsText);
        }
    }

    private static View createFriendView(android.content.Context context, FriendConcertDTO friend, boolean isAttending) {
        View friendView = android.view.LayoutInflater.from(context).inflate(R.layout.item_friend_concert, null);
        
        ImageView avatar = friendView.findViewById(R.id.friend_avatar);
        TextView name = friendView.findViewById(R.id.friend_name);
        TextView status = friendView.findViewById(R.id.friend_status);

        name.setText(friend.getUsername());
        
        if (friend.getProfileImageUrl() != null && !friend.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(friend.getProfileImageUrl())
                    .placeholder(R.drawable.ic_person)
                    .into(avatar);
        }

        if (isAttending) {
            status.setText("Attending");
            status.setBackgroundResource(R.drawable.status_attending_background);
        } else {
            status.setText("Want to go");
            status.setBackgroundResource(R.drawable.status_want_to_go_background);
        }

        return friendView;
    }
}