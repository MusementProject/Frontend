package com.example.musementfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.UserDTO;
import com.example.musementfrontend.dto.UserRequestLoginDTO;
import com.example.musementfrontend.dto.UserRequestLoginWithGoogle;
import com.example.musementfrontend.dto.UserResponseLoginDTO;
import com.example.musementfrontend.util.GoogleConfig;
import com.example.musementfrontend.util.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    private final static String googleClientId = BuildConfig.GOOGLE_CLIENT_ID;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private SignInButton googleSignInButton;
    public  ActivityResultLauncher<Intent> googleSignInResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        task.getResult(ApiException.class);
                        finish();
                        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
                        if (googleSignInAccount != null) {
                            sendInfo(googleSignInAccount);
                        }
                    } catch (ApiException e) {
                        Log.e("GoogleSignIn", "Google sign-in failed", e);
                        Toast.makeText(Login.this, "Something went wrong: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
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
                .requestIdToken(GoogleConfig.getClientId())
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            sendInfo(googleSignInAccount);
        }
        googleSignInButton.setOnClickListener(v -> {
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
        EditText usernameField = Util.getCustomEditViewById(this, R.id.username);
        EditText passwordField = Util.getCustomEditViewById(this, R.id.password);
        if (isValidLoginData(usernameField, passwordField)) {
            UserRequestLoginDTO userLogin = new UserRequestLoginDTO(usernameField.getText().toString(), passwordField.getText().toString());
            APIService apiService = APIClient.getClient().create(APIService.class);
            Call<UserResponseLoginDTO> call = apiService.userLogin(userLogin);
            call.enqueue(new Callback<UserResponseLoginDTO>() {
                @Override
                public void onResponse(Call<UserResponseLoginDTO> call, Response<UserResponseLoginDTO> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(Login.this, Feed.class);
                        startActivity(intent);
                        finish();
                    } // if not - other answers
                }

                @Override
                public void onFailure(Call<UserResponseLoginDTO> call, Throwable t) {
                    Toast toast = Toast.makeText(Login.this, "Failure: " + t.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }

    private boolean isValidLoginData(EditText loginField, EditText passwordField) {
        if (loginField.getText().toString().isBlank() || passwordField.getText().toString().isBlank()) {
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

    private void sendInfo(GoogleSignInAccount googleAccout){
        String accessToken = googleAccout.getIdToken();
        if (accessToken != null){
            APIService apiService = APIClient.getClient().create(APIService.class);
            UserRequestLoginWithGoogle userRequest = new UserRequestLoginWithGoogle(accessToken);
            Call<UserResponseLoginDTO> call = apiService.userLoginWithGoogle(userRequest);
            call.enqueue(new Callback<UserResponseLoginDTO>() {
                @Override
                public void onResponse(Call<UserResponseLoginDTO> call, Response<UserResponseLoginDTO> response) {
                    if (response.isSuccessful()){
                        UserResponseLoginDTO result = response.body();
                        Log.d("token", accessToken);
                        if (result != null) {
                            UserDTO user = new UserDTO();
                            user.setId(result.getId());
                            user.setEmail(result.getEmail());
                            user.setUsername(result.getUsername());
                            user.setAccessToken(accessToken);
                            Log.d("token", accessToken);
                            Intent intent = new Intent(Login.this, Feed.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponseLoginDTO> call, Throwable t) {
                    Toast toast = Toast.makeText(Login.this, "Failure: " + t.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }
}