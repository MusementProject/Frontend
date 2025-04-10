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
import com.example.musementfrontend.dto.UserDTO;

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
        UserDTO user;
        if (arguments != null){
            user = (UserDTO) arguments.get(IntentKeys.getUSER_KEY());
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
        Bundle arguments = activity.getIntent().getExtras();
        UserDTO user;
        if (arguments != null){
            user = (UserDTO) arguments.get(IntentKeys.getUSER_KEY());
        }else{
            user = null;
        }
        bell.setOnClickListener(view -> OnClickBell(view, user));
        musement_icon.setOnClickListener(view -> OnClickMusementIcon(view, user));
    }

    static public void OnClickBell(View view, UserDTO user){
        Context context = view.getContext();
        if(context.getClass() == Notification.class){
            return;
        }
        Intent intent = new Intent(context, Notification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static public void OnClickMusementIcon(View view, UserDTO user){
        Context context = view.getContext();
        if(context.getClass() == Feed.class){
            return;
        }
        Intent intent = new Intent(context, Feed.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static private void OnClickFeed(View view, UserDTO user){
        Context context = view.getContext();
        if(context.getClass() == Feed.class){
            return;
        }
        Intent intent = new Intent(context, Feed.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static private void OnClickRecommendation(View view, UserDTO user){
        Context context = view.getContext();
        if(context.getClass() == Recommendation.class){
            return;
        }
        Intent intent = new Intent(context, Recommendation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static private void OnClickInvitation(View view, UserDTO user){
        Context context = view.getContext();
        if(context.getClass() == Invitation.class){
            return;
        }
        Intent intent = new Intent(context, Invitation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static private void OnClickProfile(View view, UserDTO user){
        Context context = view.getContext();
        if(context.getClass() == Profile.class){
            return;
        }
        Intent intent = new Intent(context, Profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        fillIntent(intent, user);
        context.startActivity(intent);
    }

    static private void fillIntent(Intent intent, UserDTO user){
        if (user != null){
            intent.putExtra(IntentKeys.getUSER_KEY(), user);
        }
    }
}
