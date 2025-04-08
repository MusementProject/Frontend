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

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.dto.UserRegisterDTO;
import com.example.musementfrontend.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        Util.setIcon(this);
        initLoginField();
        initUsernameField();
        initEmailField();
        initPasswordField();
        initRepeatPasswordField();
    }

    public void initLoginField() {
        EditText loginField = Util.getCustomEditViewById(this, R.id.login);
        loginField.setHint("Login");
    }

    public void initUsernameField() {
        EditText usernameField = Util.getCustomEditViewById(this, R.id.username);
        usernameField.setHint("Username");
    }

    public void initEmailField() {
        EditText emailField = Util.getCustomEditViewById(this, R.id.email);
        emailField.setHint("Email");
    }

    public void initPasswordField() {
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        passwordField.setHint("Password");
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void initRepeatPasswordField() {
        EditText repeatPasswordField = Util.getCustomEditViewById(this, R.id.repeat_password);
        repeatPasswordField.setHint("Repeat password");
        repeatPasswordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repeatPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void onClickRegisterButton(View view) {
        EditText loginField = Util.getCustomEditViewById(this, R.id.login);
        EditText usernameField = Util.getCustomEditViewById(this, R.id.username);
        EditText emailField = Util.getCustomEditViewById(this, R.id.email);
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        EditText repeatPasswordField = Util.getCustomEditViewById(this, R.id.repeat_password);


        if (isValidRegisterData(loginField, usernameField, emailField, passwordField, repeatPasswordField)) {

            UserRegisterDTO userRegister = new UserRegisterDTO(loginField.getText().toString(),
                    usernameField.getText().toString(), emailField.getText().toString(), passwordField.getText().toString());
            APIService apiService = APIClient.getClient().create(APIService.class);
            Call<UserDTO> call = apiService.userRegister(userRegister);
            call.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Intent intent = new Intent(Registration.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Registration.this, "Registration failed!", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Toast toast = Toast.makeText(Registration.this, "Failure: " + t.getMessage(), Toast.LENGTH_LONG);
                    System.out.println(t.getMessage());
                    toast.show();
                }
            });
        }
    }

    private boolean isValidRegisterData(EditText loginField,EditText usernameField, EditText emailField, EditText passwordField, EditText repeatPasswordField ){
        if (loginField.getText().toString().isBlank() || usernameField.getText().toString().isBlank()||
                emailField.getText().toString().isBlank() || passwordField.getText().toString().isBlank() ||
                repeatPasswordField.getText().toString().isBlank()) {
            Toast toast = Toast.makeText(this, "Please, fill in all fields", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        if (!(passwordField.getText().toString().equals(repeatPasswordField.getText().toString()))) {
            Toast toast = Toast.makeText(this, "Passwords are not equal", Toast.LENGTH_LONG);
            passwordField.setText("");
            repeatPasswordField.setText("");
            toast.show();
            return false;
        }
        return true;
    }
}