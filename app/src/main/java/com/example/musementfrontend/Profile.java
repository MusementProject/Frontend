package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        UtilButtons.Init(this);
        setUserAvatar();

        User user = Util.getUser(getIntent());
        if (user != null) {
            TextView name = findViewById(R.id.name);
            name.setText(user.getUsername());
        }
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
                    return true;
                }
                if (itemId == R.id.log_out){
                    logOut();
                    return true;
                }
                return true;
            });
            menu.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAttendingConcerts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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
        ImageView avatar = findViewById(R.id.avatar);
        Glide.with(this)
                .load("https://zefirka.club/uploads/posts/2023-01/1673278260_2-zefirka-club-p-serii-chelovek-na-avu-2.png")
                .circleCrop()
                .into(avatar);
    }

    private void showLoading(String message) {
        TextView loadingText = findViewById(R.id.loading_text);
        loadingText.setText(message);
        loadingText.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        TextView loadingText = findViewById(R.id.loading_text);
        loadingText.setVisibility(View.GONE);
    }

    private void loadAttendingConcerts() {
        // showLoading("Loading concerts...");
        User user = Util.getUser(getIntent());
        if (user == null) return;
        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<List<Concert>> call = apiService.getAttendingConcerts("Bearer " + user.getAccessToken(), user.getId());
        call.enqueue(new Callback<List<Concert>>() {
            @Override
            public void onResponse(Call<List<Concert>> call, Response<List<Concert>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UtilFeed.FillProfileConcerts(Profile.this, response.body());
                    hideLoading();
                } else {
                    showLoading("Failed to load concerts");
                }
            }
            @Override
            public void onFailure(Call<List<Concert>> call, Throwable t) {
                showLoading("Failed to load concerts");
            }
        });
    }

    public void OnClickFriends(View view) {}
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
    public void OnClickSocialNetworks(View view) {}
    public void OnClickProfileSettings(View view) {}
}
