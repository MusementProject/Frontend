package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dialogs.FriendsDialogFragment;
import com.example.musementfrontend.dto.FriendDTO;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Concert;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;
import com.example.musementfrontend.util.UtilFeed;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    User user;
    Bundle arguments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        UtilButtons.Init(this);
        setUserAvatar();

        fillUserConcerts();

        User user = Util.getUser(getIntent());
        if (user != null) {
            fillUserInfo(user);
        }
        this.user = user;
        ImageButton settings = findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(this, view);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.profile_settings){
                    Intent intent = new Intent(Profile.this, ProfileSettings.class);
                    intent.putExtra(IntentKeys.getUSER_KEY(), user);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (itemId == R.id.log_out){
                    logOut();
                    finish();
                    return true;
                }
                return true;
            });
            menu.show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void fillUserInfo(User user){
        TextView name = findViewById(R.id.name);
        TextView nickname = findViewById(R.id.nickname);
        name.setText(user.getUsername());
        nickname.setText("@" + user.getNickname());
    }

    private void logOut(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null){
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                    Intent intent = new Intent(Profile.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
    }

    private void setUserAvatar() {
        // get info about user avatar
        ImageView avatar = findViewById(R.id.avatar);
        Glide.with(this)
                .load(Util.getProfilePhotoDefault())
                .circleCrop()
                .into(avatar);
    }

    private void fillUserConcerts() {
        // get user concerts from database!!
        List<Concert> concerts = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            concerts.add(new Concert(1, 1, "https://vdnh.ru/upload/resize_cache/iblock/edb/1000_1000_1/edb1fcf17e7b3933296993fac951fd9c.jpg", "A2", new Date(1000)));
        }
        UtilFeed.FillFeedConcert(this, concerts);
    }

    public void OnClickFriends(View view) {
//        List<FriendDTO> friendList = Arrays.asList(
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(1L, "amiasleep", "Малышева Анастасия", null, true),
//                new FriendDTO(2L, "amiasleep", "Малышева Анастасия", null, true)
//        );

        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<List<FriendDTO>> call = apiService.getAllUserFriends("Bearer " + user.getAccessToken(), user.getId());
        call.enqueue(new Callback<List<FriendDTO>>() {
            @Override
            public void onResponse(Call<List<FriendDTO>> call, Response<List<FriendDTO>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<FriendDTO> friendList= response.body();
                    FriendsDialogFragment dialog = new FriendsDialogFragment();
                    dialog.setFriends(friendList);
                    dialog.show(getSupportFragmentManager(), "friends");
                }
            }

            @Override
            public void onFailure(Call<List<FriendDTO>> call, Throwable t) {
                Log.e("Friends", "API call failed", t);
            }
        });
    }

    public void OnClickTickets(View view) {
        Intent intent = new Intent(this, Tickets.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void OnClickPlaylists(View view) {
        Intent intent = new Intent(this, Playlists.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        User user = Util.getUser(getIntent());
        intent.putExtra(IntentKeys.getUSER_KEY(), user);
        startActivity(intent);
    }

    public void OnClickSocialNetworks(View view) {

    }

    public void OnClickProfileSettings(View view) {

    }
}