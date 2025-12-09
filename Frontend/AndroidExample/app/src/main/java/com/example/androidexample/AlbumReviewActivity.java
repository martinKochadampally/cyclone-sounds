package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AlbumReviewActivity extends AppCompatActivity {

    private TextView headerText;
    private ImageView albumCoverImage; // New variable
    private RatingBar ratingBar;
    private EditText bestSongInput;
    private EditText worstSongInput;
    private EditText reviewInput;
    private Button submitButton;
    private String loggedInUsername;

    private Button homeButton;
    private Button musicButton;
    private Button jamsButton;
    private Button createButton;
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_review);

        loggedInUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");

        headerText = findViewById(R.id.review_header);
        albumCoverImage = findViewById(R.id.review_album_cover); // Initialize Image
        ratingBar = findViewById(R.id.album_rating_bar);
        bestSongInput = findViewById(R.id.best_song_input);
        worstSongInput = findViewById(R.id.worst_song_input);
        reviewInput = findViewById(R.id.review_input);
        submitButton = findViewById(R.id.submit_review_btn);

        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        setupNavigation();

        String albumName = getIntent().getStringExtra("ALBUM_NAME");
        if (albumName != null) {
            headerText.setText("Review: " + albumName);
        }

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String bestSong = bestSongInput.getText().toString();
            String worstSong = worstSongInput.getText().toString();
            String reviewText = reviewInput.getText().toString();

            Toast.makeText(AlbumReviewActivity.this, "Rating: " + rating + "/5 Submitted!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupNavigation() {
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumReviewActivity.this, HomeActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        musicButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumReviewActivity.this, MusicActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumReviewActivity.this, CreateActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        jamsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumReviewActivity.this, JamsActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumReviewActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            intent.putExtra("PROFILE_TO_VIEW", loggedInUsername);
            startActivity(intent);
        });
    }
}