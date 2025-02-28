package com.example.musementfrontend.util;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.musementfrontend.Feed;
import com.example.musementfrontend.Invitation;
import com.example.musementfrontend.Notification;
import com.example.musementfrontend.Profile;
import com.example.musementfrontend.R;
import com.example.musementfrontend.Recommendation;
import com.example.musementfrontend.Registration;
import com.example.musementfrontend.pojo.Concert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Util {

    static public void setIcon(AppCompatActivity activity){
        ImageView imageView = activity.findViewById(R.id.musement_icon);

        String filename = "musement_icon.png";

        try(InputStream inputStream = activity.getApplicationContext().getAssets().open(filename)){
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    static public EditText getCustomEditViewById(AppCompatActivity activity, int id){
        View view = activity.findViewById(id);
        return view.findViewById(R.id.edit_text_view);
    }

    static public void Init(AppCompatActivity activity){
        InitHeader(activity);
        InitMainMenu(activity);
    }

    static private void InitMainMenu(AppCompatActivity activity){
        ConstraintLayout mainMenu = activity.findViewById(R.id.main_menu);
        ImageButton feed = mainMenu.findViewById(R.id.feed);
        ImageButton recommendation = mainMenu.findViewById(R.id.recommendation);
        ImageButton invitation = mainMenu.findViewById(R.id.invitation);
        ImageButton profile = mainMenu.findViewById(R.id.profile);

        feed.setOnClickListener(Util::OnClickFeed);
        recommendation.setOnClickListener(Util::OnClickRecommendation);
        invitation.setOnClickListener(Util::OnClickInvitation);
        profile.setOnClickListener(Util::OnClickProfile);

    }

    static private void InitHeader(AppCompatActivity activity){
        ConstraintLayout header = activity.findViewById(R.id.header);
        ImageButton bell = header.findViewById(R.id.bell);
        ImageButton musement_icon = header.findViewById(R.id.musement_icon);

        bell.setOnClickListener(Util::OnClickBell);
        musement_icon.setOnClickListener(Util::OnClickMusementIcon);
    }

    static public void FillFeedConcert(AppCompatActivity activity,List<Concert> concerts){
        ScrollView scroll = activity.findViewById(R.id.scroll);
        ConstraintLayout layout = scroll.findViewById(R.id.feed_item);
        LinearLayout feed = layout.findViewById(R.id.feed);

        for(Concert concert : concerts){
            View concertView = activity.getLayoutInflater().inflate(R.layout.concert_item, feed, false);
            ImageButton concertImage = concertView.findViewById(R.id.concert);
            Glide.with(activity)
                    .load(concert.getImageUrl())
                    .into(concertImage);

            TextView artist = concertView.findViewById(R.id.artist);
            TextView location = concertView.findViewById(R.id.location);
            TextView date = concertView.findViewById(R.id.date);
            artist.setText("Dima Bilan"); // change!!!
            location.setText(concert.getLocation());
            date.setText(concert.getDate().toString());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) concertView.getLayoutParams();

            params.setMargins(0, 0,0 , 80);
            concertView.setLayoutParams(params);
            feed.addView(concertView);
        }
    }

    static public void OnClickBell(View view){
        Context context = view.getContext();
        if(context.getClass() == Notification.class){
            return;
        }
        Intent intent = new Intent(context, Notification.class);
        context.startActivity(intent);
    }

    static public void OnClickMusementIcon(View view){
        Context context = view.getContext();
        if(context.getClass() == Feed.class){
            return;
        }
        Intent intent = new Intent(context, Feed.class);
        context.startActivity(intent);
    }

    static private void OnClickFeed(View view){
        Context context = view.getContext();
        if(context.getClass() == Feed.class){
            return;
        }
        Intent intent = new Intent(context, Feed.class);
        context.startActivity(intent);
    }

    static private void OnClickRecommendation(View view){
        Context context = view.getContext();
        if(context.getClass() == Recommendation.class){
            return;
        }
        Intent intent = new Intent(context, Recommendation.class);
        context.startActivity(intent);
    }

    static private void OnClickInvitation(View view){
        Context context = view.getContext();
        if(context.getClass() == Invitation.class){
            return;
        }
        Intent intent = new Intent(context, Invitation.class);
        context.startActivity(intent);
    }

    static private void OnClickProfile(View view){
        Context context = view.getContext();
        if(context.getClass() == Profile.class){
            return;
        }
        Intent intent = new Intent(context, Profile.class);
        context.startActivity(intent);
    }
}
