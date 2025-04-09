package com.example.musementfrontend;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musementfrontend.pojo.Playlist;
import com.example.musementfrontend.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Playlists extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlists);
        UtilButtons.Init(this);
        FillPlaylists(this, null);

        Button btnAddNewPlaylist = findViewById(R.id.add_playlist_button);
        btnAddNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPlaylistDialog();
            }
        });
    }

    private void showAddPlaylistDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new playlist!");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_playlist, null);
        final EditText linkEditText = dialogView.findViewById(R.id.spotify_link_input);
        final EditText titleEditText = dialogView.findViewById(R.id.playlist_title);

        builder.setView(dialogView);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String spotifyLink = linkEditText.getText().toString().trim();
                String playlistTitle = linkEditText.getText().toString();
                if (!spotifyLink.isEmpty() && !playlistTitle.isEmpty()) {
                    // parse link
                    dialog.cancel();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    static public void FillPlaylists(AppCompatActivity activity, List<Playlist> playlists){
        ScrollView scroll = activity.findViewById(R.id.scroll);
        ConstraintLayout layout = scroll.findViewById(R.id.feed_item);
        LinearLayout feed = layout.findViewById(R.id.feed);

        if (playlists == null || playlists.isEmpty()) { // remove

            ArrayList<String> playlistNames = new ArrayList<>(Arrays.asList(
                    "My 2017",
                    "Crazy Vibe",
                    "Midnight Chill",
                    "Rainy Window Thoughts",
                    "Dancing in My Room",
                    "Lost in Daydreams",
                    "Sunny Mood Boost",
                    "Autumn Sadness",
                    "Alone but Okay",
                    "Soft Chaos",
                    "Main Character Energy"
            ));

            playlists = new ArrayList<>();
            for (int i = 0; i < playlistNames.size(); i++) {
                Playlist p = new Playlist(0, playlistNames.get(i), null);
                playlists.add(p);
            }
        }

        int layoutId = R.layout.playlist_item;
        for (Playlist playlist : playlists) {
            View playlistView = activity.getLayoutInflater().inflate(layoutId, feed, false);
            TextView playlistName = playlistView.findViewById(R.id.playlist_name);
            playlistName.setText(playlist.getTitle());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) playlistView.getLayoutParams();
            params.setMargins(0, 0, 0, 60);
            playlistView.setLayoutParams(params);
            feed.addView(playlistView);
        }
    }
}
