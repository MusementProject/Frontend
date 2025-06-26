package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dialogs.FriendsDialogFragment;
import com.example.musementfrontend.dto.UserDTO;
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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {
    private User user;
    private UserDTO userDTO;
    private APIService api;
    private TextView username;
    private TextView nickname;
    private ImageView avatar;
    private ActivityResultLauncher<Intent> profileSettingsLauncher;
    private String accessToken;
    private long userId;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UtilButtons.Init(this);
        Bundle arguments = getIntent().getExtras();
        user = null;
        if (arguments != null) {
            user = (User) arguments.get(IntentKeys.getUSER_KEY());
        }

        profileSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        String newUsername = data.getStringExtra("username");
                        String newBio = data.getStringExtra("bio");
                        String newNick = data.getStringExtra("nickname");
                        String newAvatar = data.getStringExtra("profilePicture");

                        userDTO.setUsername(newUsername);
                        userDTO.setBio(newBio);
                        userDTO.setNickname(newNick);
                        userDTO.setProfilePicture(newAvatar);

                        username.setText(
                                getString(R.string.username_handle, userDTO.getUsername())
                        );
                        nickname.setText(newNick);
                        setUserAvatar();
                    }
                }
        );

        api = APIClient.getClient().create(APIService.class);
        Intent intent = getIntent();
        accessToken = intent.getStringExtra("accessToken");
        userId = intent.getLongExtra("userId", 0L);

        userDTO = new UserDTO(
                intent.getStringExtra("username"),
                intent.getStringExtra("email"),
                intent.getStringExtra("bio"),
                intent.getStringExtra("nickname"),
                null
        );

        username = findViewById(R.id.username);
        nickname = findViewById(R.id.nickname);
        avatar = findViewById(R.id.avatar);
        ImageButton settings = findViewById(R.id.settings);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        username.setText(
                getString(R.string.username_handle, userDTO.getUsername())
        );
        nickname.setText(userDTO.getNickname());
        setUserAvatar();

        ProfilePagerAdapter pagerAdapter = new ProfilePagerAdapter(this, accessToken, userId);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Attending");
                    break;
                case 1:
                    tab.setText("Wishlist");
                    break;
            }
        }).attach();

        api.getCurrentUser("Bearer " + accessToken)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User serverUser = response.body();

                            userDTO.setUsername(serverUser.getUsername());
                            userDTO.setEmail(serverUser.getEmail());
                            userDTO.setBio(serverUser.getBio());
                            userDTO.setNickname(serverUser.getNickname());
                            userDTO.setProfilePicture(serverUser.getProfilePicture());

                            username.setText(
                                    getString(R.string.username_handle, userDTO.getUsername())
                            );
                            nickname.setText(userDTO.getNickname());
                            setUserAvatar();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "null";
                                Log.e("Profile", "Error loading user data: code="
                                        + response.code()
                                        + " message=" + response.message()
                                        + " errorBody=" + errorBody);
                            } catch (IOException e) {
                                Log.e("Profile", "Error reading error body", e);
                            }
                            Toast.makeText(Profile.this,
                                    "Error loading profile: " + response.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.e("Profile", "Error during network request", t);
                        Toast.makeText(Profile.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

        settings.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(this, view);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.profile_settings) {
                    Intent intentSettings = getIntentSettings();
                    profileSettingsLauncher.launch(intentSettings);
                    return true;
                }
                if (itemId == R.id.log_out) {
                    logOut();
                    return true;
                }
                return false;
            });
            menu.show();
        });

        Log.d("token", accessToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager != null && viewPager.getAdapter() != null) {
            ProfilePagerAdapter newAdapter = new ProfilePagerAdapter(this, accessToken, userId);
            viewPager.setAdapter(newAdapter);
            
            int currentItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem);
        }
    }

    @NonNull
    private Intent getIntentSettings() {
        Intent intentSettings = new Intent(Profile.this, ProfileSettings.class);
        intentSettings.putExtra("username", userDTO.getUsername());
        intentSettings.putExtra("email", userDTO.getEmail());
        intentSettings.putExtra("bio", userDTO.getBio());
        intentSettings.putExtra("nickname", userDTO.getNickname());
        intentSettings.putExtra("profilePicture", userDTO.getProfilePicture());
        intentSettings.putExtra("accessToken", accessToken);
        intentSettings.putExtra("userId", userId);
        return intentSettings;
    }

    private void setUserAvatar() {
        String url = userDTO.getProfilePicture();
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .circleCrop()
                    .into(avatar);
        } else {
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .circleCrop()
                    .into(avatar);
        }
    }

    private void logOut() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                finish();
                Intent intent = new Intent(Profile.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }

    public void OnClickFriends(View view) {
        FriendsDialogFragment dialog = new FriendsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(IntentKeys.getUSER_KEY(), user);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "friends");
    }

    public void OnClickTickets(View view) {
        Intent intent = new Intent(this, Tickets.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(IntentKeys.getUSER_KEY(), user);
        UtilButtons.fillIntent(intent, user);
        Log.d("Profile", "########## Opening Tickets activity with user: " + user);
        startActivity(intent);
    }

    public void OnClickPlaylists(View view) {
        Intent intent = new Intent(this, Playlists.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(IntentKeys.getUSER_KEY(), user);
        UtilButtons.fillIntent(intent, user);
        startActivity(intent);
    }

    public void OnClickSocialNetworks(View view) {}

    public void OnClickProfileSettings(View view) {}
}