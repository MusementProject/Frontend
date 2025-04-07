package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.dto.UserLoginDTO;
import com.example.musementfrontend.util.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    String googleClientId = BuildConfig.GOOGLE_CLIENT_ID;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    SignInButton googleSignInButton;
    ActivityResultLauncher<Intent> googleSignInResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK){
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try{
                        task.getResult(ApiException.class);
                        finish();
                        Intent intent = new Intent(Login.this, Feed.class);
                        startActivity(intent);
                    }catch (ApiException e){
                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Util.setIcon(this);
        initLoginField();
        initPasswordField();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            finish();
            Intent intent = new Intent(this, Feed.class);
            startActivity(intent);
        }
        Log.d("Google sign in", googleClientId);
        googleSignInButton.setOnClickListener(v -> {
            Log.d("GoogleSignIn", "Google SignIn button clicked");
            Intent intent = googleSignInClient.getSignInIntent();
            googleSignInResult.launch(intent);
        });
    }

    public void initLoginField() {
        EditText loginField = Util.getCustomEditViewById(this, R.id.username);
        loginField.setHint("Enter your username");
    }

    public void initPasswordField() {
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        passwordField.setHint("Enter your password");
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void onClickSignInButton(View view) {
        // future: send data to back, if successful -> sign in, if not -> show mistake
        EditText usernameField = Util.getCustomEditViewById(this, R.id.username);
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        if (isValidLoginData(usernameField, passwordField)) {
            UserLoginDTO userLogin = new UserLoginDTO(usernameField.getText().toString(), passwordField.getText().toString());
            APIService apiService = APIClient.getClient().create(APIService.class);
            Call<UserDTO> call = apiService.userLogin(userLogin);
            call.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(Login.this, Feed.class);
                        startActivity(intent);
                        finish();
                    } // if not - other answers
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Toast toast = Toast.makeText(Login.this, "Failure: " + t.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }

    private boolean isValidLoginData(EditText loginField, EditText passwordField) {
        if (loginField.getText().toString().isBlank()|| passwordField.getText().toString().isBlank()) {
            Toast toast = Toast.makeText(this, "Incorrect registration data", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
        return true;
    }

    public void onClickSignUpButton(View view) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }
}