package com.example.musementfrontend;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.client.APIClient;
import com.example.client.APIService;
import com.example.client.pojo.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView greetingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        greetingText = findViewById(R.id.textView);

        getUserData();
    }

    private void getUserData(){
        APIService apiservice = APIClient.getClient().create(APIService.class);

        Call<User> call = apiservice.getUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    User user = response.body();
                    if (user != null){
                        greetingText.setText("Hello, " +  user.getName());
                    }
                }else{
                    greetingText.setText("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                greetingText.setText("Failure: " + t.getMessage());
            }
        });
    }
}