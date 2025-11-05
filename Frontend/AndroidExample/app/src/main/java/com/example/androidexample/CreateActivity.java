package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CreateActivity extends AppCompatActivity {

    private Button profileButton;
    private Button homeButton;
    private Button musicButton;
    private Button jamsButton;

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        homeButton = findViewById(R.id.home_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            currentUsername = intent.getStringExtra("USERNAME");
        }

        homeButton.setOnClickListener(view -> {
            Intent homeIntent = new Intent(CreateActivity.this, HomeActivity.class);
            homeIntent.putExtra("USERNAME", currentUsername);
            startActivity(homeIntent);
        });

        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(CreateActivity.this, ProfileActivity.class);
            profileIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            profileIntent.putExtra("PROFILE_TO_VIEW", currentUsername);
            startActivity(profileIntent);
        });

        musicButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(CreateActivity.this, MusicActivity.class);
            musicIntent.putExtra("USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        jamsButton.setOnClickListener(view -> {
            Intent jamsIntent = new Intent(CreateActivity.this, JamsActivity.class);
            jamsIntent.putExtra("USERNAME", currentUsername);
            startActivity(jamsIntent);
        });
    }
}