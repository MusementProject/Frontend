package com.example.musementfrontend.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.musementfrontend.Feed;
import com.example.musementfrontend.Invitation;
import com.example.musementfrontend.Notification;
import com.example.musementfrontend.Profile;
import com.example.musementfrontend.R;
import com.example.musementfrontend.Recommendation;

public class UtilButtons {
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

        feed.setOnClickListener(UtilButtons::OnClickFeed);
        recommendation.setOnClickListener(UtilButtons::OnClickRecommendation);
        invitation.setOnClickListener(UtilButtons::OnClickInvitation);
        profile.setOnClickListener(UtilButtons::OnClickProfile);

    }

    static private void InitHeader(AppCompatActivity activity){
        ConstraintLayout header = activity.findViewById(R.id.header);
        ImageButton bell = header.findViewById(R.id.bell);
        ImageButton musement_icon = header.findViewById(R.id.musement_icon);

        bell.setOnClickListener(UtilButtons::OnClickBell);
        musement_icon.setOnClickListener(UtilButtons::OnClickMusementIcon);
    }

    static public void OnClickBell(View view){
        Context context = view.getContext();
        if(context.getClass() == Notification.class){
            return;
        }
        Intent intent = new Intent(context, Notification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    static public void OnClickMusementIcon(View view){
        Context context = view.getContext();
        if(context.getClass() == Feed.class){
            return;
        }
        Intent intent = new Intent(context, Feed.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    static private void OnClickFeed(View view){
        Context context = view.getContext();
        if(context.getClass() == Feed.class){
            return;
        }
        Intent intent = new Intent(context, Feed.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    static private void OnClickRecommendation(View view){
        Context context = view.getContext();
        if(context.getClass() == Recommendation.class){
            return;
        }
        Intent intent = new Intent(context, Recommendation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    static private void OnClickInvitation(View view){
        Context context = view.getContext();
        if(context.getClass() == Invitation.class){
            return;
        }
        Intent intent = new Intent(context, Invitation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    static private void OnClickProfile(View view){
        Context context = view.getContext();
        if(context.getClass() == Profile.class){
            return;
        }
        Intent intent = new Intent(context, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

}
