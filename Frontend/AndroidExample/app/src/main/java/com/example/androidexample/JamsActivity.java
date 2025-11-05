package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JamsActivity extends AppCompatActivity {

    private Button profileButton;
    private Button homeButton;
    private Button musicButton;
    private Button createButton;
    private Button friendsButton;

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jams);

        homeButton = findViewById(R.id.home_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        // friendsButton = findViewById(R.id.friends_button_btn);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            currentUsername = intent.getStringExtra("USERNAME");
        }

        homeButton.setOnClickListener(view -> {
            Intent homeIntent = new Intent(JamsActivity.this, HomeActivity.class);
            homeIntent.putExtra("USERNAME", currentUsername);
            startActivity(homeIntent);
        });

        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(JamsActivity.this, ProfileActivity.class);
            profileIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            profileIntent.putExtra("PROFILE_TO_VIEW", currentUsername);
            startActivity(profileIntent);
        });

        musicButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(JamsActivity.this, MusicActivity.class);
            musicIntent.putExtra("USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        createButton.setOnClickListener(view -> {
            Intent createIntent = new Intent(JamsActivity.this, CreateActivity.class);
            createIntent.putExtra("USERNAME", currentUsername);
            startActivity(createIntent);
        });
    }
}