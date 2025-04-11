package com.example.musementfrontend;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_settings);

        // Button saveButton = findViewById(R.id.saveButton);
        // saveButton.setOnClickListener(v -> {
        //     // Логика сохранения настроек
        // });
    }
}
