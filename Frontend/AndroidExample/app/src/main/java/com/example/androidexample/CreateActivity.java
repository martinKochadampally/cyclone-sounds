package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

/**
 * This activity serves as a creation hub, providing the user with options to create
 * different types of content, such as reviews or playlists.
 */
public class CreateActivity extends AppCompatActivity {
    // UI Elements for content creation and navigation
    private Button createReviewButton;
    private Button createPlaylistButton;
    private Button profileButton;
    private Button musicButton;
    private Button jamsButton;
    private Button homeButton;

    // Holds the username of the logged-in user.
    private String currentUsername;

    /**
     * Called when the activity is first created. Initializes UI components, retrieves the
     * logged-in username, and sets up click listeners for all buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Initialize buttons from the layout.
        createReviewButton = findViewById(R.id.create_review_btn);
        createPlaylistButton = findViewById(R.id.create_playlist_btn);
        musicButton = findViewById(R.id.music_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        homeButton = findViewById(R.id.home_button_btn);

        // Get the username from the intent.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOGGED_IN_USERNAME")) {
            currentUsername = intent.getStringExtra("LOGGED_IN_USERNAME");
        } else {
            // Handle the case where the username is not passed, though in a real app,
            // you might want to redirect to login or show an error.
        }

        // Set listener for the "Create Review" button.
        createReviewButton.setOnClickListener(view -> {
            Intent createReviewIntent = new Intent(CreateActivity.this, CreateReviewActivity.class);
            createReviewIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(createReviewIntent);
        });

        // Set listener for the "Create Playlist" button.
        createPlaylistButton.setOnClickListener(view -> {
            Intent createPlaylistIntent = new Intent(CreateActivity.this, CreatePlaylistActivity.class);
            createPlaylistIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(createPlaylistIntent);
        });

        // Set listeners for the main navigation buttons.
        homeButton.setOnClickListener(view -> {
            Intent homeIntent = new Intent(CreateActivity.this, HomeActivity.class);
            homeIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(homeIntent);
        });

        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(CreateActivity.this, ProfileActivity.class);
            profileIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            profileIntent.putExtra("PROFILE_TO_VIEW", currentUsername); // Navigate to own profile.
            startActivity(profileIntent);
        });

        musicButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(CreateActivity.this, MusicActivity.class);
            musicIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        jamsButton.setOnClickListener(view -> {
            Intent jamsIntent = new Intent(CreateActivity.this, JamsActivity.class);
            jamsIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(jamsIntent);
        });
    }
}
