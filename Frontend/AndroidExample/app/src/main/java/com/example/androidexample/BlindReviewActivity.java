package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BlindReviewActivity extends AppCompatActivity {

    private TextView songNameText;
    private TextView artistNameText;
    private Button playButton;
    private RatingBar ratingBar;
    private EditText reviewInput;
    private Button submitButton;
    private String loggedInUsername;

    // Bottom Nav Buttons
    private Button homeButton;
    private Button musicButton;
    private Button jamsButton;
    private Button createButton;
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_review);

        loggedInUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");

        // Initialize Views
        songNameText = findViewById(R.id.blind_song_name);
        artistNameText = findViewById(R.id.blind_artist_name);
        playButton = findViewById(R.id.blind_play_btn);
        ratingBar = findViewById(R.id.blind_rating_bar);
        reviewInput = findViewById(R.id.blind_review_input);
        submitButton = findViewById(R.id.submit_blind_review_btn);

        // Initialize Bottom Nav
        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        setupNavigation();

        // Placeholder: Set dummy song data (Later this will come from backend)
        songNameText.setText("Mystery Song");
        artistNameText.setText("Mystery Artist");

        playButton.setOnClickListener(v -> {
            Toast.makeText(BlindReviewActivity.this, "Play functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String review = reviewInput.getText().toString();
            Toast.makeText(BlindReviewActivity.this, "Rated: " + rating + " stars. Review Submitted!", Toast.LENGTH_SHORT).show();

            // Optional: clear inputs
            reviewInput.setText("");
            ratingBar.setRating(0);
        });
    }

    private void setupNavigation() {
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlindReviewActivity.this, HomeActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        musicButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlindReviewActivity.this, MusicActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlindReviewActivity.this, CreateActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        jamsButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlindReviewActivity.this, JamsActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(BlindReviewActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            intent.putExtra("PROFILE_TO_VIEW", loggedInUsername);
            startActivity(intent);
        });
    }
}