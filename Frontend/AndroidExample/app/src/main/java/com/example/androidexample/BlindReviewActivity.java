package com.example.androidexample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BlindReviewActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    private TextView songNameText;
    private TextView artistNameText;
    private Button playButton;
    private RatingBar ratingBar;
    private EditText reviewInput;
    private Button submitButton;
    private String loggedInUsername;

    private Button homeButton;
    private Button musicButton;
    private Button jamsButton;
    private Button createButton;
    private Button profileButton;

    private RequestQueue requestQueue;

    private int currentSongId = -1;
    private String currentSongName = "Loading...";
    private String currentArtistName = "";
    private String currentEmbedUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_review);

        loggedInUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");

        if (loggedInUsername == null) {
            Toast.makeText(this, "Warning: Not logged in (Username is null)", Toast.LENGTH_LONG).show();
        }

        requestQueue = Volley.newRequestQueue(this);

        songNameText = findViewById(R.id.blind_song_name);
        artistNameText = findViewById(R.id.blind_artist_name);
        playButton = findViewById(R.id.blind_play_btn);
        ratingBar = findViewById(R.id.blind_rating_bar);
        reviewInput = findViewById(R.id.blind_review_input);
        submitButton = findViewById(R.id.submit_blind_review_btn);

        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        setupNavigation();

        fetchNextBlindSong();

        playButton.setOnClickListener(v -> {
            if (currentEmbedUrl != null && !currentEmbedUrl.isEmpty()) {
                showSpotifyDialog(currentEmbedUrl);
            } else {
                Toast.makeText(this, "No song loaded to play.", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(v -> {
            if (currentSongId == -1) {
                Toast.makeText(this, "No song to review.", Toast.LENGTH_SHORT).show();
                return;
            }
            submitReview();
        });
    }

    private void fetchNextBlindSong() {
        String url = BASE_URL + "/blind-review/next?username=" + loggedInUsername;
        Log.d("BlindReview", "Fetching URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        currentSongId = response.getInt("songId");
                        currentSongName = response.getString("songName");
                        currentArtistName = response.getString("artist");
                        currentEmbedUrl = response.getString("embedURL");

                        songNameText.setText(currentSongName);
                        artistNameText.setText(currentArtistName);

                    } catch (JSONException e) {
                        Log.e("BlindReview", "JSON Parsing error", e);
                        songNameText.setText("Error loading song");
                        Toast.makeText(BlindReviewActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;

                        if (statusCode == 204) {
                            songNameText.setText("No more songs!");
                            artistNameText.setText("You've reviewed everything.");
                            currentSongId = -1;
                            currentEmbedUrl = "";
                        } else {
                            Toast.makeText(BlindReviewActivity.this, "Server Error Code: " + statusCode, Toast.LENGTH_LONG).show();
                            Log.e("BlindReview", "Server Error: " + statusCode);
                        }
                    } else {
                        String errorMsg = error.getMessage() != null ? error.getMessage() : error.toString();
                        Toast.makeText(BlindReviewActivity.this, "Connection Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("BlindReview", "Volley error: " + error.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void submitReview() {
        String url = BASE_URL + "/blind-review/review";
        JSONObject reviewJson = new JSONObject();
        try {
            reviewJson.put("username", loggedInUsername);
            reviewJson.put("songId", currentSongId);
            reviewJson.put("rating", ratingBar.getRating());
            reviewJson.put("reviewText", reviewInput.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, reviewJson,
                response -> {
                    Toast.makeText(BlindReviewActivity.this, "Review Submitted!", Toast.LENGTH_SHORT).show();
                    reviewInput.setText("");
                    ratingBar.setRating(0);
                    fetchNextBlindSong();
                },
                error -> {
                    Toast.makeText(BlindReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                    Log.e("BlindReview", "Submit error: " + error.toString());
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void showSpotifyDialog(String embedUrl) {
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.loadUrl(embedUrl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Now Playing: " + currentSongName);
        builder.setView(webView);

        builder.setNegativeButton("Close Player", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                webView.destroy();
                dialog.dismiss();
            }
        });

        builder.setOnDismissListener(dialog -> webView.destroy());

        builder.show();
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