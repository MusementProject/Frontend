package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musementfrontend.util.Util;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        Util.setIcon(this);
        initLoginField();
        initUsernameField();
        initEmailField();
        initPasswordField();
        initRepeatPasswordField();
    }

    public void initLoginField(){
        EditText loginField = Util.getCustomEditViewById(this, R.id.login);
        loginField.setHint("Login");
    }

    public void initUsernameField(){
        EditText usernameField = Util.getCustomEditViewById(this, R.id.username);
        usernameField.setHint("Username");
    }

    public void initEmailField(){
        EditText emailField = Util.getCustomEditViewById(this, R.id.email);
        emailField.setHint("Email");
    }

    public void initPasswordField(){
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        passwordField.setHint("Password");
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void initRepeatPasswordField(){
        EditText repeatPasswordField = Util.getCustomEditViewById(this, R.id.repeat_password);
        repeatPasswordField.setHint("Repeat password");
        repeatPasswordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repeatPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void onClickRegisterButton(View view){
        EditText loginField = Util.getCustomEditViewById(this, R.id.login);
        EditText usernameField = Util.getCustomEditViewById(this, R.id.username);
        EditText emailField = Util.getCustomEditViewById(this, R.id.email);
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        EditText repeatPasswordField = Util.getCustomEditViewById(this, R.id.password);

        if(loginField.getText().length() == 0 || usernameField.getText().length() == 0 || emailField.getText().length() == 0 || passwordField.getText().length() == 0 || repeatPasswordField.getText().length() == 0){
            Toast toast = Toast.makeText(this, "Please, fill in all fields", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if(!passwordField.getText().equals(repeatPasswordField.getText())){
            Toast toast = Toast.makeText(this, "Passwords are not equal", Toast.LENGTH_LONG);
            passwordField.setText("");
            repeatPasswordField.setText("");
            toast.show();
            return;
        }
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}