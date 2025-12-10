package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * The main screen of the application after a user has logged in.
 * It provides navigation to various features of the app such as Music, Jams, Profile, etc.
 */
public class HomeActivity extends AppCompatActivity {

    // UI elements
    private TextView messageText;
    private TextView usernameText;
    private Button profileButton;
    private Button musicButton;
    private Button jamsButton;
    private Button createButton;
    private Button myPlaylistsButton;
    private Button friendsButton;
    private ImageButton searchButton;

    // The username of the currently logged-in user.
    private String currentUsername;

    /**
     * Called when the activity is first created. Initializes the UI elements,
     * retrieves the logged-in username, and sets up click listeners for navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI components.
        messageText = findViewById(R.id.home_msg_txt);
        usernameText = findViewById(R.id.home_username_txt);
        musicButton = findViewById(R.id.music_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        myPlaylistsButton = findViewById(R.id.my_playlists_btn);
        friendsButton = findViewById(R.id.friends_button_btn);
        searchButton = findViewById(R.id.search_button);

        // Get the username from the intent.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOGGED_IN_USERNAME")) {
            currentUsername = intent.getStringExtra("LOGGED_IN_USERNAME");
            messageText.setText("Welcome");
            usernameText.setText(currentUsername);
        } else {
            messageText.setText("Cyclone Sounds");
            usernameText.setVisibility(View.INVISIBLE);
        }

        // Set up click listener for the "My Playlists" button.
        myPlaylistsButton.setOnClickListener(view -> {
            Intent myPlaylistsIntent = new Intent(HomeActivity.this, MyPlaylistsActivity.class);
            myPlaylistsIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(myPlaylistsIntent);
        });

        // Set up click listener for the search button.
        searchButton.setOnClickListener(view -> {
            Intent searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
            searchIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(searchIntent);
        });

        // Set up click listener for the "Music" button.
        musicButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(HomeActivity.this, MusicActivity.class);
            musicIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        // Set up click listener for the "Profile" button.
        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            profileIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            profileIntent.putExtra("PROFILE_TO_VIEW", currentUsername); // View own profile
            startActivity(profileIntent);
        });

        // Set up click listener for the "Jams" button.
        jamsButton.setOnClickListener(view -> {
            Intent jamsIntent = new Intent(HomeActivity.this, JamsActivity.class);
            jamsIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(jamsIntent);
        });

        // Set up click listener for the "Create" button.
        createButton.setOnClickListener(view -> {
            Intent createIntent = new Intent(HomeActivity.this, CreateActivity.class);
            createIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(createIntent);
        });

        // Set up click listener for the "Friends" button.
        friendsButton.setOnClickListener(view -> {
            Intent friendsIntent = new Intent(HomeActivity.this, FriendsActivity.class);
            friendsIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(friendsIntent);
        });
    }
}
