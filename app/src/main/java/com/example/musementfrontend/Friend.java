package com.example.musementfrontend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.UtilButtons;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Friend extends AppCompatActivity {

    User user = null;
    String friendUsername = null;
    User friend = null;
    Bundle arguments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        UtilButtons.Init(this);
        arguments = getIntent().getExtras();
        if (arguments != null){
            user = arguments.getParcelable(IntentKeys.getUSER());
            friendUsername = arguments.getString(IntentKeys.getFRIEND_USERNAME());
        }

        getFriendUserInfo();
    }

    private void getFriendUserInfo(){
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<User> call = apiService.getUserByUsername("Bearer " + user.getAccessToken(), friendUsername);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null){
                    friend = response.body();
                    fillInfo();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Friends", "Error while getting friend page");
            }
        });
    }

    private void fillInfo(){
        TextView friendName = findViewById(R.id.name);
        TextView friendUsername = findViewById(R.id.username);
        friendName.setText(friend.getUsername());
        friendUsername.setText(friend.getUsername());
        setUserAvatar();
    }

    private void setUserAvatar(){
        ImageView friendAvatar = findViewById(R.id.avatar);
        if (friendAvatar == null){
            return;
        }
        String url = friend.getProfilePicture();
        if (url != null && !url.isEmpty()){
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

    public void onClickAddFriend(View view){

    }

    public void onClickDeleteFriend(View view){

    }
}