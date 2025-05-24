package com.example.musementfrontend.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.musementfrontend.dto.User;

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

        Bundle arguments = activity.getIntent().getExtras();
        User user;
        if (arguments != null){
            user = (User) arguments.get(IntentKeys.getUSER());
        } else {
            user = null;
        }
        feed.setOnClickListener(view -> OnClickFeed(view, user));
        recommendation.setOnClickListener(view -> OnClickRecommendation(view, user));
        invitation.setOnClickListener(view -> OnClickInvitation(view, user));
        profile.setOnClickListener(view -> OnClickProfile(view, user));

    }

    static private void InitHeader(AppCompatActivity activity){
        ConstraintLayout header = activity.findViewById(R.id.header);
        ImageButton bell = header.findViewById(R.id.bell);
        ImageButton musement_icon = header.findViewById(R.id.musement_icon);
        User user = Util.getUser(activity.getIntent());
        bell.setOnClickListener(view -> OnClickBell(view, user));
        musement_icon.setOnClickListener(view -> OnClickMusementIcon(view, user));
    }

    static public void OnClickBell(View view, User user){
        Context context = view.getContext();
        if(context.getClass() == Notification.class){
            return;
        }
        Intent intent = new Intent(context, Notification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static public void OnClickMusementIcon(View view, User user){
        Context context = view.getContext();
        if(context.getClass() == Feed.class){
            return;
        }
        Intent intent = new Intent(context, Feed.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static private void OnClickFeed(View view, User user){
        Context context = view.getContext();
        if(context.getClass() == Feed.class){
            return;
        }
        Intent intent = new Intent(context, Feed.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        intent.putExtra("accessToken", user.getAccessToken());
        context.startActivity(intent);
    }

    static private void OnClickRecommendation(View view, User user){
        Context context = view.getContext();
        if(context.getClass() == Recommendation.class){
            return;
        }
        Intent intent = new Intent(context, Recommendation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static private void OnClickInvitation(View view, User user){
        Context context = view.getContext();
        if(context.getClass() == Invitation.class){
            return;
        }
        Intent intent = new Intent(context, Invitation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static private void OnClickProfile(View view, User user){
        Context context = view.getContext();
        if(context.getClass() == Profile.class){
            return;
        }
        Intent intent = new Intent(context, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    public static void fillIntent(Intent intent, User user){
        if (user != null){
            intent.putExtra(IntentKeys.getUSER(), user);
        }
    }
}
