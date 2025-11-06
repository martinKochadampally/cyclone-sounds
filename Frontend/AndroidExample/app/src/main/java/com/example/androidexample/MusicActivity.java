package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MusicActivity extends AppCompatActivity {

    private Button profileButton;
    private Button homeButton;
    private Button jamsButton;
    private Button createButton;

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        homeButton = findViewById(R.id.home_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            currentUsername = intent.getStringExtra("USERNAME");
        }

        homeButton.setOnClickListener(view -> {
            Intent homeIntent = new Intent(MusicActivity.this, HomeActivity.class);
            homeIntent.putExtra("USERNAME", currentUsername);
            startActivity(homeIntent);
        });

        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(MusicActivity.this, ProfileActivity.class);
            profileIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            profileIntent.putExtra("PROFILE_TO_VIEW", currentUsername);
            startActivity(profileIntent);
        });

        jamsButton.setOnClickListener(view -> {
            Intent jamsIntent = new Intent(MusicActivity.this, JamsActivity.class);
            jamsIntent.putExtra("USERNAME", currentUsername);
            startActivity(jamsIntent);
        });

        createButton.setOnClickListener(view -> {
            Intent createIntent = new Intent(MusicActivity.this, CreateActivity.class);
            createIntent.putExtra("USERNAME", currentUsername);
            startActivity(createIntent);
        });
    }
}