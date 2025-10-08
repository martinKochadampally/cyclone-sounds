package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, songEditText, genreEditText, artistEditText, bioEditText;
    private Button updateButton, deleteButton;
    private Button homeButton, musicButton, createButton, jamsButton, profileButton;

    private String currentUsername;
    private String currentUserEmail;

    // Your specific IP address
    private static final String BASE_URL = "http://10:48:165:186:8080/profiles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEditText = findViewById(R.id.profile_name_edt);
        emailEditText = findViewById(R.id.profile_email_edt);
        songEditText = findViewById(R.id.profile_song_edt);
        genreEditText = findViewById(R.id.profile_genre_edt);
        artistEditText = findViewById(R.id.profile_artist_edt);
        bioEditText = findViewById(R.id.profile_bio_edt);

        updateButton = findViewById(R.id.profile_update_btn);
        deleteButton = findViewById(R.id.profile_delete_btn);
        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            currentUsername = intent.getStringExtra("USERNAME");
            currentUserEmail = currentUsername + "@iastate.edu";
            fetchUserData();
        } else {
            Toast.makeText(this, "Error: No user profile found.", Toast.LENGTH_LONG).show();
            finish();
        }

        updateButton.setOnClickListener(v -> updateUserData());
        deleteButton.setOnClickListener(v -> deleteUserAccount());

        setupNavigation();
    }

    private void fetchUserData() {
        String url = BASE_URL + currentUserEmail;

        // Use JsonObjectRequest to correctly handle the JSON response
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Use optString for safety in case a field is missing
                        nameEditText.setText(response.optString("name"));
                        emailEditText.setText(response.optString("email"));
                        songEditText.setText(response.optString("favSong"));
                        genreEditText.setText(response.optString("favGenre"));
                        artistEditText.setText(response.optString("favArtist"));
                        bioEditText.setText(response.optString("biography"));
                    } catch (Exception e) {
                        Log.e("JSON_FETCH_ERROR", "Error parsing user data", e);
                    }
                },
                error -> {
                    Log.e("VOLLEY_FETCH_ERROR", "Could not fetch user data", error);
                    Toast.makeText(this, "Could not load profile data", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void updateUserData() {
        String url = BASE_URL + currentUserEmail;
        JSONObject profileData = new JSONObject();
        try {
            profileData.put("name", nameEditText.getText().toString());
            profileData.put("email", emailEditText.getText().toString());
            profileData.put("favSong", songEditText.getText().toString());
            profileData.put("favGenre", genreEditText.getText().toString());
            profileData.put("favArtist", artistEditText.getText().toString());
            profileData.put("biography", bioEditText.getText().toString());
        } catch (JSONException e) {
            // This should not happen
        }

        final String requestBody = profileData.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("VOLLEY_UPDATE_ERROR", "Could not update user data", error);
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void deleteUserAccount() {
        String url = BASE_URL + currentUserEmail;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    if (response.contains("success")) {
                        Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Deletion Failed", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VOLLEY_DELETE_ERROR", "Could not delete account", error);
                    Toast.makeText(this, "Deletion Failed", Toast.LENGTH_SHORT).show();
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void setupNavigation() {
        homeButton.setOnClickListener(view -> navigateTo(HomeActivity.class));
        musicButton.setOnClickListener(view -> navigateTo(MusicActivity.class));
        createButton.setOnClickListener(view -> navigateTo(CreateActivity.class));
        jamsButton.setOnClickListener(view -> navigateTo(JamsActivity.class));
        profileButton.setOnClickListener(view -> {
            Toast.makeText(this, "You are already on the Profile page.", Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(ProfileActivity.this, activityClass);
        intent.putExtra("USERNAME", currentUsername);
        startActivity(intent);
    }
}