package com.example.musementfrontend;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musementfrontend.util.Util;

import java.io.IOException;
import java.io.InputStream;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Util.setIcon(this);
        initLoginField();
        initPasswordField();
    }

    public void initLoginField(){
        EditText loginField = Util.getCustomEditViewById(this, R.id.login);
        loginField.setHint("Enter your login");
    }

    public void initPasswordField(){
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        passwordField.setHint("Enter your password");
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void onClickSignInButton(View view){
        // future: send data to back, if successful -> sign in, if not -> show mistake
        EditText loginField = Util.getCustomEditViewById(this, R.id.login);
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        if (loginField.getText().length() == 0 || passwordField.getText().length() == 0){
            Toast toast = Toast.makeText(this, "Incorrect registration data", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        Intent intent = new Intent(this, Feed.class);
        startActivity(intent);

        finish();
    }

    public void onClickSignUpButton(View view){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

}