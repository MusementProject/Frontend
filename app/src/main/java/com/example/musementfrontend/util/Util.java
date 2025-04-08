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

    private static final String ICON_FILENAME = "musement_icon.png";

    static public void setIcon(AppCompatActivity activity){
        ImageView imageView = activity.findViewById(R.id.musement_icon);

        try(InputStream inputStream = activity.getApplicationContext().getAssets().open(ICON_FILENAME)){
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
}
