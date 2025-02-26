package com.example.musementfrontend.util;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.musementfrontend.Feed;
import com.example.musementfrontend.Invitation;
import com.example.musementfrontend.Profile;
import com.example.musementfrontend.R;
import com.example.musementfrontend.Recommendation;
import com.example.musementfrontend.Registration;

import java.io.IOException;
import java.io.InputStream;

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

    static public void InitMainMenu(ConstraintLayout layout){
        Button feed = layout.findViewById(R.id.feed);
        Button recommendation = layout.findViewById(R.id.recommendation);
        Button invitation = layout.findViewById(R.id.invitation);
        Button profile = layout.findViewById(R.id.profile);

        feed.setOnClickListener(Util::OnClickFeed);
        recommendation.setOnClickListener(Util::OnClickRecommendation);
        invitation.setOnClickListener(Util::OnClickInvitation);
        profile.setOnClickListener(Util::OnClickProfile);

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
