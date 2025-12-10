package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class AlbumReviewActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    private TextView headerText;
    private RatingBar ratingBar;
    private EditText bestSongInput;
    private EditText worstSongInput;
    private EditText reviewInput;
    private Button submitButton;

    private String loggedInUsername;
    private int albumId = -1;

    private RequestQueue requestQueue;

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
        albumId = getIntent().getIntExtra("ALBUM_ID", -1);
        String albumName = getIntent().getStringExtra("ALBUM_NAME");

        requestQueue = Volley.newRequestQueue(this);

        headerText = findViewById(R.id.review_header);
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

        if (albumName != null) {
            headerText.setText("Review: " + albumName);
        } else {
            headerText.setText("Review Album");
        }

        submitButton.setOnClickListener(v -> {
            if (albumId == -1) {
                Toast.makeText(this, "Error: Album ID missing", Toast.LENGTH_SHORT).show();
                return;
            }
            submitAlbumReview();
        });
    }

    private void submitAlbumReview() {
        String url = BASE_URL + "/album-reviews/submit";

        JSONObject reviewJson = new JSONObject();
        try {
            reviewJson.put("username", loggedInUsername);
            reviewJson.put("albumId", albumId);
            reviewJson.put("rating", ratingBar.getRating());
            reviewJson.put("reviewText", reviewInput.getText().toString());
            reviewJson.put("bestSong", bestSongInput.getText().toString());
            reviewJson.put("worstSong", worstSongInput.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, reviewJson,
                response -> {
                    Toast.makeText(AlbumReviewActivity.this, "Review Submitted Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String errorMsg = error.getMessage();
                    if (error.networkResponse != null) {
                        errorMsg = "Status Code: " + error.networkResponse.statusCode;
                    }
                    Log.e("AlbumReview", "Submit Error: " + error.toString());
                    Toast.makeText(AlbumReviewActivity.this, "Failed to submit: " + errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
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