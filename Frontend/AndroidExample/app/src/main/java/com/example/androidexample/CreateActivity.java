package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private Button profileButton;
    private Button homeButton;
    private Button musicButton;
    private Button jamsButton;
    private EditText songName;
    private EditText artistName;
    private EditText rating;
    private EditText description;
    private Button submitButton;
    private String currentUsername;

    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/review";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        homeButton = findViewById(R.id.home_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        songName = findViewById(R.id.song_name_edt);
        artistName = findViewById(R.id.arist_name_edt);
        rating = findViewById(R.id.rating_edt);
        description = findViewById(R.id.description_edt);
        submitButton = findViewById(R.id.submit_btn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("USERNAME");
        }

        homeButton.setOnClickListener(view -> navigateTo(HomeActivity.class));
        profileButton.setOnClickListener(view -> navigateTo(ProfileActivity.class));
        musicButton.setOnClickListener(view -> navigateTo(MusicActivity.class));
        jamsButton.setOnClickListener(view -> navigateTo(JamsActivity.class));

        submitButton.setOnClickListener(view -> {
            String song = songName.getText().toString().trim();
            String artist = artistName.getText().toString().trim();
            String rate = rating.getText().toString().trim();
            String desc = description.getText().toString().trim();

            if (song.isEmpty() || artist.isEmpty() || rate.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                double ratingValue = Double.parseDouble(rate);
                if (ratingValue > 5 || ratingValue < 0) {
                    Toast.makeText(getApplicationContext(), "Rating must be between 0 and 5", Toast.LENGTH_LONG).show();
                    return;
                }
                makePostRequest(currentUsername, song, artist, rate, desc);
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Please enter a valid number for rating", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makePostRequest(final String user, final String songName, final String artistName, final String rating, final String description) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ,
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(getApplicationContext(), "Review Submitted Successfully!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CreateActivity.this, MusicActivity.class);
                    intent.putExtra("USERNAME", user);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Submission Error", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("reviewer", user);
                params.put("songName", songName);
                params.put("artistName", artistName);
                params.put("rating", rating);
                params.put("description", description); // Backend expects 'description'
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(CreateActivity.this, activityClass);
        intent.putExtra("USERNAME", currentUsername);
        startActivity(intent);
    }
}