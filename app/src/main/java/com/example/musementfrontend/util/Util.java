package com.example.musementfrontend.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musementfrontend.R;
import com.example.musementfrontend.dto.User;

import java.io.IOException;
import java.io.InputStream;

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

    static public User getUser(Intent intent){
        Bundle arguments = intent.getExtras();
        if (arguments != null){
            return (User) arguments.get(IntentKeys.getUSER_KEY());
        }
        return null;
    }
}
