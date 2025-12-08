package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private TextView messageText;
    private TextView usernameText;
    private Button profileButton;
    private Button musicButton;
    private Button jamsButton;
    private Button createButton;
    private Button myPlaylistsButton;
    private Button friendsButton;
    private Button albumsButton;
    private ImageButton searchButton;

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        messageText = findViewById(R.id.home_msg_txt);
        usernameText = findViewById(R.id.home_username_txt);
        musicButton = findViewById(R.id.music_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        myPlaylistsButton = findViewById(R.id.my_playlists_btn);
        friendsButton = findViewById(R.id.friends_button_btn);
        albumsButton = findViewById(R.id.albums_button_btn);
        searchButton = findViewById(R.id.search_button);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOGGED_IN_USERNAME")) {
            currentUsername = intent.getStringExtra("LOGGED_IN_USERNAME");
            messageText.setText("Welcome");
            usernameText.setText(currentUsername);
        } else {
            messageText.setText("Cyclone Sounds");
            usernameText.setVisibility(View.INVISIBLE);
        }

        myPlaylistsButton.setOnClickListener(view -> {
            Intent myPlaylistsIntent = new Intent(HomeActivity.this, MyPlaylistsActivity.class);
            myPlaylistsIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(myPlaylistsIntent);
        });

        albumsButton.setOnClickListener(view -> {
            Intent albumsIntent = new Intent(HomeActivity.this, AlbumsActivity.class);
            albumsIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(albumsIntent);
        });

        searchButton.setOnClickListener(view -> {
            Intent searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
            searchIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(searchIntent);
        });

        musicButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(HomeActivity.this, MusicActivity.class);
            musicIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            profileIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            profileIntent.putExtra("PROFILE_TO_VIEW", currentUsername);
            startActivity(profileIntent);
        });

        jamsButton.setOnClickListener(view -> {
            Intent jamsIntent = new Intent(HomeActivity.this, JamsActivity.class);
            jamsIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(jamsIntent);
        });

        createButton.setOnClickListener(view -> {
            Intent createIntent = new Intent(HomeActivity.this, CreateActivity.class);
            createIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(createIntent);
        });

        friendsButton.setOnClickListener(view -> {
            Intent friendsIntent = new Intent(HomeActivity.this, FriendsActivity.class);
            friendsIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(friendsIntent);
        });
    }
}