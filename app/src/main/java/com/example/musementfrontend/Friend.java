package com.example.musementfrontend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.FriendDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.UtilButtons;
import com.example.musementfrontend.util.UtilFeed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Friend extends AppCompatActivity {

    private User user = null;
    private String friendUsername = null;
    private User friend = null;
    private Bundle arguments;
    private Button addButton;
    private Button deleteButton;
    private final APIService apiService = APIClient.getClient().create(APIService.class);
    private Boolean areFriends = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        UtilButtons.Init(this);
        arguments = getIntent().getExtras();
        if (arguments != null) {
            user = arguments.getParcelable(IntentKeys.getUSER());
            friendUsername = arguments.getString(IntentKeys.getFRIEND_USERNAME());
        }
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        addButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        getFriendUserInfo();
    }

    private void getFriendUserInfo() {
        Call<User> call = apiService.getUserByUsername("Bearer " + user.getAccessToken(), friendUsername);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friend = response.body();
                    fillInfo();
                    setFriendshipStatus();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Friends", "Error while getting friend page");
            }
        });
    }

    private void fillInfo() {
        TextView friendName = findViewById(R.id.name);
        TextView friendUsername = findViewById(R.id.username);
        friendName.setText(friend.getUsername());
        friendUsername.setText(friend.getUsername());
        setUserAvatar();
    }

    private void setUserAvatar() {
        ImageView friendAvatar = findViewById(R.id.avatar);
        if (friendAvatar == null) {
            return;
        }
        String url = friend.getProfilePicture();
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .circleCrop()
                    .into(friendAvatar);
        } else {
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .circleCrop()
                    .into(friendAvatar);
        }
    }

    private void setFriendshipStatus() {
        Call<Boolean> call = apiService.getFriendshipInfo("Bearer " + user.getAccessToken(), user.getId(), friend.getId());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    areFriends = response.body();
                    setButton();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e("Friends", "Friends info can not be found");
            }
        });
    }

    private void setButton() {
        if (areFriends) {
            addButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    public void onClickAddFriend(View view) {
        Call<FriendDTO> call = apiService.addFriend("Bearer " + user.getAccessToken(), user.getId(), friend.getId());
        call.enqueue(new Callback<FriendDTO>() {
            @Override
            public void onResponse(@NonNull Call<FriendDTO> call, @NonNull Response<FriendDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    areFriends = true;
                    setButton();
                }
            }

            @Override
            public void onFailure(Call<FriendDTO> call, Throwable t) {
                Log.e("Friends", "Cannot add friend");
            }
        });
    }

    public void onClickDeleteFriend(View view) {
        Call<Boolean> call = apiService.deleteFriend("Bearer " + user.getAccessToken(), user.getId(), friend.getId());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    areFriends = false;
                    setButton();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e("Friends", "Cannot delete friend");
            }
        });
    }
}