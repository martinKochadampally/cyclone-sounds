package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreateActivity extends AppCompatActivity {
    private Button createReviewButton;
    private Button createPlaylistButton;
    private Button profileButton;
    private Button musicButton;
    private Button jamsButton;
    private Button homeButton;

    private String currentUsername; // Variable to safely hold the username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        createReviewButton = findViewById(R.id.create_review_btn);
        createPlaylistButton = findViewById(R.id.create_playlist_btn);

        musicButton = findViewById(R.id.music_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        homeButton = findViewById(R.id.home_button_btn);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            currentUsername = intent.getStringExtra("USERNAME");
        } else {

        }

        createReviewButton.setOnClickListener(view -> {
            Intent createReviewIntent = new Intent(CreateActivity.this, CreateReviewActivity.class);
            createReviewIntent.putExtra("USERNAME", currentUsername);
            startActivity(createReviewIntent);
        });

        createPlaylistButton.setOnClickListener(view -> {
            Intent createPlaylistIntent = new Intent(CreateActivity.this, CreateActivity.class);
            createPlaylistIntent.putExtra("USERNAME", currentUsername);
            startActivity(createPlaylistIntent);
        });



        musicButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(CreateActivity.this, MusicActivity.class);
            musicIntent.putExtra("USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(CreateActivity.this, ProfileActivity.class);
            profileIntent.putExtra("USERNAME", currentUsername);
            startActivity(profileIntent);
        });

        jamsButton.setOnClickListener(view -> {
            Intent jamsIntent = new Intent(CreateActivity.this, JamsActivity.class);
            jamsIntent.putExtra("USERNAME", currentUsername);
            startActivity(jamsIntent);
        });

        homeButton.setOnClickListener(view -> {
            Intent createIntent = new Intent(CreateActivity.this, HomeActivity.class);
            createIntent.putExtra("USERNAME", currentUsername);
            startActivity(createIntent);
        });
    }
}