package com.example.musementfrontend;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musementfrontend.pojo.Artist;
import com.example.musementfrontend.pojo.Playlist;
import com.example.musementfrontend.util.Util;
import java.util.List;
import java.util.Map;
import com.bumptech.glide.Glide;
import com.example.musementfrontend.util.UtilButtons;

import android.widget.ImageView;


public class PlaylistStatistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist_statistics);
        UtilButtons.Init(this);

        Intent intent = getIntent();
        Playlist playlist = (Playlist) intent.getSerializableExtra("playlist");
        if(playlist == null) {
            Toast.makeText(this, "Playlist not found", Toast.LENGTH_SHORT).show();
            playlist = new Playlist(0, "Test Playlist", null);
        }

        TextView titleTextView = findViewById(R.id.playlistTitle);
        String prefix = "Top artists by ";
        String playlistTitle = playlist.getTitle();
        SpannableString spannable = new SpannableString(prefix + playlistTitle);
        spannable.setSpan(new StyleSpan(Typeface.ITALIC),
                prefix.length(),
                prefix.length() + playlistTitle.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleTextView.setText(spannable);

        View feedContainer = findViewById(R.id.feed_item);

        LinearLayout feedLayout = feedContainer.findViewById(R.id.feed);

        List<Map.Entry<Artist, Integer>> sortedArtists = playlist.getSortedArtistSongCounts();
        LayoutInflater inflater = LayoutInflater.from(this);

        int index = 0;
        for (Map.Entry<Artist, Integer> entry : sortedArtists) {
            View artistItem = inflater.inflate(R.layout.artist_item, feedLayout, false);
            TextView artistNameTv = artistItem.findViewById(R.id.artist_name);
            TextView songCountTv = artistItem.findViewById(R.id.song_count);

            ImageView artistPhoto = artistItem.findViewById(R.id.artist_photo);
            Artist artist = entry.getKey();
            Glide.with(this)
                    .load(artist.getImageUrl())
                    .into(artistPhoto);

            int count = entry.getValue();
            artistNameTv.setText(artist.getName());
            songCountTv.setText(count + (count == 1 ? " song" : " songs"));

            if (index < 3) {
                artistItem.setBackgroundResource(R.drawable.rounded_green);
            }
            index++;

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) artistItem.getLayoutParams();

            params.setMargins(0, 20,0 , 20);
            artistItem.setLayoutParams(params);
            feedLayout.addView(artistItem);
        }
    }
}