package com.example.androidexample;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class SpotifyPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_player);

        WebView webView = findViewById(R.id.spotify_web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Spotify's player requires JavaScript
        webView.setWebViewClient(new WebViewClient()); // Ensures links open within the WebView

        String embedUrl = getIntent().getStringExtra("EMBED_URL");

        if (embedUrl != null && !embedUrl.isEmpty()) {
            webView.loadUrl(embedUrl);
        } else {
            // Handle the error, maybe show a message and close the activity
            finish();
        }
    }
}
