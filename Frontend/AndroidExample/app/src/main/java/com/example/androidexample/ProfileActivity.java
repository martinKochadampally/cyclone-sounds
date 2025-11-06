package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameEditText, songEditText, genreEditText, artistEditText, bioEditText;
    private Button updateButton, deleteButton, logoutButton;
    private Button homeButton, musicButton, createButton, jamsButton, profileButton;
    private TextView profileTitleTextView; // TextView declaration

    private String profileToViewUsername;
    private String loggedInUsername;

    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/profiles/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameEditText = findViewById(R.id.profile_name_edt);
        songEditText = findViewById(R.id.profile_song_edt);
        genreEditText = findViewById(R.id.profile_genre_edt);
        artistEditText = findViewById(R.id.profile_artist_edt);
        bioEditText = findViewById(R.id.profile_bio_edt);

        // Initialize the title TextView
        profileTitleTextView = findViewById(R.id.profile_title_txt);

        updateButton = findViewById(R.id.profile_update_btn);
        deleteButton = findViewById(R.id.profile_delete_btn);
        logoutButton = findViewById(R.id.profile_logout_btn);
        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOGGED_IN_USERNAME") && intent.hasExtra("PROFILE_TO_VIEW")) {
            loggedInUsername = intent.getStringExtra("LOGGED_IN_USERNAME");
            profileToViewUsername = intent.getStringExtra("PROFILE_TO_VIEW");

            // Dynamic Title Logic: Set the text for the on-screen TextView
            if (loggedInUsername.equals(profileToViewUsername)) {
                profileTitleTextView.setText("Edit Your Profile");
            } else {
                profileTitleTextView.setText(profileToViewUsername + "\'s Profile");
            }

            if(getSupportActionBar() != null) {
                // This still sets the title for the ActionBar/Toolbar
                getSupportActionBar().setTitle(profileToViewUsername + "\'s Profile");
            }

            fetchUserData();
            setupNavigation();

            if (!loggedInUsername.equals(profileToViewUsername)) {
                makeProfileReadOnly();
            }

        } else {
            Toast.makeText(this, "Error: No user profile found.", Toast.LENGTH_LONG).show();
            finish();
        }

        updateButton.setOnClickListener(v -> updateUserData());
        deleteButton.setOnClickListener(v -> deleteUserAccount());
        logoutButton.setOnClickListener(v -> {
            Intent logoutIntent = new Intent(ProfileActivity.this, LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void makeProfileReadOnly() {
        nameEditText.setEnabled(false);
        songEditText.setEnabled(false);
        genreEditText.setEnabled(false);
        artistEditText.setEnabled(false);
        bioEditText.setEnabled(false);

        updateButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);

        homeButton.setVisibility(View.VISIBLE);
        musicButton.setVisibility(View.VISIBLE);
        createButton.setVisibility(View.VISIBLE);
        jamsButton.setVisibility(View.VISIBLE);
        profileButton.setVisibility(View.VISIBLE);
    }

    private void fetchUserData() {
        String url = BASE_URL + profileToViewUsername;
        Log.d("ProfileFetch", "Fetching data from URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        nameEditText.setText(response.optString("name"));
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
                    Toast.makeText(this, "Could not load profile data.", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void updateUserData() {
        String url = BASE_URL + profileToViewUsername;
        JSONObject profileData = new JSONObject();
        try {
            profileData.put("name", nameEditText.getText().toString());
            profileData.put("favSong", songEditText.getText().toString());
            profileData.put("favGenre", genreEditText.getText().toString());
            profileData.put("favArtist", artistEditText.getText().toString());
            profileData.put("biography", bioEditText.getText().toString());
            profileData.put("username", profileToViewUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, profileData,
                response -> Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show(),
                error -> {
                    Log.e("VOLLEY_UPDATE_ERROR", "Could not update user data", error);
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void deleteUserAccount() {
        String url = BASE_URL + profileToViewUsername;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
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
            if (loggedInUsername.equals(profileToViewUsername)) {
                return;
            }
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            intent.putExtra("PROFILE_TO_VIEW", loggedInUsername);
            startActivity(intent);
        });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(ProfileActivity.this, activityClass);
        intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
        startActivity(intent);
    }
}