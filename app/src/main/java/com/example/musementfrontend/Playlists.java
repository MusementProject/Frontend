package com.example.musementfrontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.PlaylistResponseDTO;
import com.example.musementfrontend.dto.SpotifyPlaylistRequest;
import com.example.musementfrontend.dto.SpotifyPlaylistResponse;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.pojo.Playlist;
import com.example.musementfrontend.pojo.PlaylistInfo;
import com.example.musementfrontend.util.IntentKeys;
import com.example.musementfrontend.util.Util;
import com.example.musementfrontend.util.UtilButtons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Playlists extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable playlistsUpdater;
    private static final int UPDATE_INTERVAL = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlists);
        UtilButtons.Init(this);

        Button btnAddNewPlaylist = findViewById(R.id.add_playlist_button);
        btnAddNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPlaylistDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoUpdate();
    }

    private void startAutoUpdate() {
        if (playlistsUpdater == null) {
            playlistsUpdater = new Runnable() {
                @Override
                public void run() {
                    loadPlaylists();
                    handler.postDelayed(this, UPDATE_INTERVAL);
                }
            };
        }
        handler.post(playlistsUpdater);
    }

    private void stopAutoUpdate() {
        handler.removeCallbacks(playlistsUpdater);
    }

    private void loadPlaylists() {
//        User user = Util.getUser(getIntent());
        Bundle arguments = getIntent().getExtras();
        User user = null;
        if (arguments != null) {
            user = (User) arguments.get(IntentKeys.getUSER_KEY());
        }

        APIService apiService = APIClient.getClient().create(APIService.class);
        Call<List<PlaylistResponseDTO>> call = apiService.getUserPlaylists("Bearer " + user.getAccessToken(), user.getId());
        call.enqueue(new Callback<List<PlaylistResponseDTO>>() {
            @Override
            public void onResponse(Call<List<PlaylistResponseDTO>> call, Response<List<PlaylistResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlaylistResponseDTO> playlistDtos = response.body();
                    List<Playlist> playlists = new ArrayList<>();
                    for (PlaylistResponseDTO dto : playlistDtos) {
                        playlists.add(new Playlist(dto.getPlaylistId().intValue(), dto.getTitle(), dto.getPlaylistUrl(), dto.getPlaylistInfo()));
                    }
                    // чем новее плейлист (больше айдишник), тем выше
                    playlists.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
                    FillPlaylists(Playlists.this, playlists);
                }
            }
            @Override
            public void onFailure(Call<List<PlaylistResponseDTO>> call, Throwable t) {
                Toast.makeText(Playlists.this, "Failed to load playlists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddPlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new playlist!");

        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_playlist, null, false);
        final EditText linkEditText = dialogView.findViewById(R.id.spotify_link_input);
        final EditText titleEditText = dialogView.findViewById(R.id.playlist_title);

        builder.setView(dialogView);

        builder.setPositiveButton("Done", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        // for Done button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spotifyLink = linkEditText.getText().toString().trim();
                String playlistTitle = titleEditText.getText().toString().trim();

                if (spotifyLink.isEmpty() || playlistTitle.isEmpty()) {
                    Toast.makeText(Playlists.this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (playlistTitle.length() > 40) {
                    Toast.makeText(Playlists.this, "Playlist title must be at most 40 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String PREFIX = "https://open.spotify.com/playlist/";
                if (!spotifyLink.startsWith(PREFIX) || !spotifyLink.contains("?")) {
                    Toast.makeText(Playlists.this, "Invalid link", Toast.LENGTH_SHORT).show();
                    return;
                }

                // good path
                int start = PREFIX.length();
                int end = spotifyLink.indexOf("?");
                String playlistId = spotifyLink.substring(start, end);

                getNewPlaylist(playlistId, playlistTitle);
                dialog.dismiss();
            }
        });
    }

    private void getNewPlaylist(String playlistId, String playlistTitle) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        Bundle arguments = getIntent().getExtras();

        User user;
        if (arguments != null) {
            user = (User) arguments.get(IntentKeys.getUSER_KEY());
        } else {
            user = null;
        }
        if (user != null) {
            SpotifyPlaylistRequest request = new SpotifyPlaylistRequest(user.getId(), playlistId, playlistTitle);
            Call<PlaylistResponseDTO> call = apiService.addPlaylist("Bearer " + user.getAccessToken(), request);
            call.enqueue(new Callback<PlaylistResponseDTO>() {
                @Override
                public void onResponse(Call<PlaylistResponseDTO> call, Response<PlaylistResponseDTO> response) {
                    Toast.makeText(Playlists.this, "The playlist will be added soon!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<PlaylistResponseDTO> call, Throwable t) {
                    // Toast.makeText(Playlists.this, "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void FillPlaylists(AppCompatActivity activity, List<Playlist> playlists) {
        ScrollView scroll = activity.findViewById(R.id.scroll);
        ConstraintLayout layout = scroll.findViewById(R.id.feed_item);
        LinearLayout feed = layout.findViewById(R.id.feed);

        feed.removeAllViews();

        if (playlists == null || playlists.isEmpty()) {
            TextView emptyText = new TextView(activity);
            emptyText.setText("No playlists yet. Add the first one!");
            emptyText.setTextSize(20);
            emptyText.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
            emptyText.setPadding(20, 20, 20, 20);
            emptyText.setGravity(android.view.Gravity.CENTER);
            emptyText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            emptyText.setTypeface(emptyText.getTypeface(), android.graphics.Typeface.ITALIC);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(20, 20, 20, 20);
            emptyText.setLayoutParams(params);

            feed.addView(emptyText);
            return;
        }

        int layoutId = R.layout.playlist_item;
        for (final Playlist playlist : playlists) {
            View playlistView = activity.getLayoutInflater().inflate(layoutId, feed, false);
            TextView playlistName = playlistView.findViewById(R.id.playlist_name);
            playlistName.setText(playlist.getTitle());

            playlistView.setOnClickListener(v -> {
                User user = Util.getUser(activity.getIntent());
                APIService apiService = APIClient.getClient().create(APIService.class);
                Call<PlaylistResponseDTO> call = apiService.getPlaylistStats("Bearer " + user.getAccessToken(), (long) playlist.getId());
                call.enqueue(new Callback<PlaylistResponseDTO>() {
                    @Override
                    public void onResponse(Call<PlaylistResponseDTO> call, Response<PlaylistResponseDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PlaylistResponseDTO dto = response.body();
                            Playlist realPlaylist = new Playlist(dto.getPlaylistId().intValue(), dto.getTitle(), dto.getPlaylistUrl(), dto.getPlaylistInfo());
                            Intent intent = new Intent(activity, PlaylistStatistics.class);
                            intent.putExtra("playlist", realPlaylist);
                            activity.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaylistResponseDTO> call, Throwable t) {
                        Toast.makeText(activity, "Failed to load playlist stats", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) playlistView.getLayoutParams();
            params.setMargins(0, 0, 0, 60);
            playlistView.setLayoutParams(params);
            feed.addView(playlistView);
        }
    }
}
