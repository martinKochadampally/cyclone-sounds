package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SpotifyPlayerActivity extends AppCompatActivity {

    private Button closeButton;
    private Button reviewButton;
    private String currentUsername;
    private String songName;
    private String artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_player);

        WebView webView = findViewById(R.id.spotify_web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Spotify's player requires JavaScript
        webView.setWebViewClient(new WebViewClient()); // Ensures links open within the WebView

        String embedUrl = getIntent().getStringExtra("EMBED_URL");
        currentUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");

        // TODO: This is a placeholder. The song and artist name should be passed as extras.
        songName = getIntent().getStringExtra("SONG_NAME");
        artistName = getIntent().getStringExtra("ARTIST_NAME");

        closeButton = findViewById(R.id.close_button);
        reviewButton = findViewById(R.id.review_button);

        closeButton.setOnClickListener(v -> {
            finish();
        });

        reviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(SpotifyPlayerActivity.this, CreateReviewActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            intent.putExtra("SONG_NAME", songName);
            intent.putExtra("ARTIST_NAME", artistName);
            startActivity(intent);
        });

        if (embedUrl != null && !embedUrl.isEmpty()) {
            webView.loadUrl(embedUrl);
        } else {
            // Handle the error, maybe show a message and close the activity
            finish();
        }

    }
}
