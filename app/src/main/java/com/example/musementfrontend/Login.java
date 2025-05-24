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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musementfrontend.Client.APIClient;
import com.example.musementfrontend.Client.APIService;
import com.example.musementfrontend.dto.User;
import com.example.musementfrontend.dto.UserRequestLoginDTO;
import com.example.musementfrontend.dto.UserRequestLoginWithGoogle;
import com.example.musementfrontend.dto.UserResponseLoginDTO;
import com.example.musementfrontend.util.GoogleConfig;
import com.example.musementfrontend.util.IntentKeys;
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
        if (!isValidLoginData(usernameField, passwordField)) return;

        UserRequestLoginDTO userLogin = new UserRequestLoginDTO(
                usernameField.getText().toString().trim(),
                passwordField.getText().toString().trim()
        );

        APIService apiService = APIClient.getClient().create(APIService.class);
        apiService.userLogin(userLogin).enqueue(new Callback<UserResponseLoginDTO>() {
            @Override
            public void onResponse(
                    @NonNull Call<UserResponseLoginDTO> call,
                    @NonNull Response<UserResponseLoginDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(Login.this,
                            "Error: " + response.code() + " " + response.message(),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                UserResponseLoginDTO dto = response.body();
                String jwt = dto.getToken();
                if (jwt == null || jwt.isEmpty()) {
                    Toast.makeText(Login.this,
                            "Error: token is null or empty",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Switch to Profile activity, passing UserDTO data
                Intent intent = Login.this.getIntent(dto, jwt);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<UserResponseLoginDTO> call, @NonNull Throwable t) {
                Toast.makeText(Login.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @NonNull
    private Intent getIntent(UserResponseLoginDTO dto, String jwt) {
        Intent intent = new Intent(Login.this, Profile.class);
        intent.putExtra("username", dto.getUsername());
        intent.putExtra("email", dto.getEmail());
        intent.putExtra("accessToken", jwt);
        intent.putExtra("userId", dto.getId());
        return intent;
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
    private void sendInfo(GoogleSignInAccount googleAccount) {
        String accessToken = googleAccount.getIdToken();
        if (accessToken != null) {
            APIService apiService = APIClient.getClient().create(APIService.class);
            UserRequestLoginWithGoogle userRequest = new UserRequestLoginWithGoogle(accessToken);
            apiService.userLoginWithGoogle(userRequest).enqueue(new Callback<UserResponseLoginDTO>() {
                @Override
                public void onResponse(@NonNull Call<UserResponseLoginDTO> call,
                                       @NonNull Response<UserResponseLoginDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponseLoginDTO result = response.body();
                        String jwt = result.getToken();
                        if (jwt == null || jwt.isEmpty()) {
                            Toast.makeText(Login.this,
                                    "Error: token is null or empty",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Switch to Profile activity, passing UserDTO data
                        Intent intent = getIntent(result, jwt);
                        User user = new User();
                        user.setId(result.getId());
                        user.setEmail(result.getEmail());
                        user.setUsername(result.getUsername());
                        user.setAccessToken(jwt);
                        intent.putExtra(IntentKeys.getUSER(), user);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this,
                                "Error during Google login: " + response.code() + " " + response.message(),
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserResponseLoginDTO> call, @NonNull Throwable t) {
                    Toast.makeText(Login.this, "Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}