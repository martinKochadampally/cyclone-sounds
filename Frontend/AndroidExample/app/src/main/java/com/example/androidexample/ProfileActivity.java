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

/**
 * Activity to display and edit a user's profile. It can be in a read-only mode when
 * viewing another user's profile, or in an editable mode when a user is viewing their own.
 */
public class ProfileActivity extends AppCompatActivity {

    // UI elements for profile data and actions.
    private EditText nameEditText, songEditText, genreEditText, artistEditText, bioEditText;
    private Button updateButton, deleteButton, logoutButton;
    private Button homeButton, musicButton, createButton, jamsButton, profileButton;
    private TextView profileTitleTextView; // Displays the title of the profile page.

    // Usernames for the logged-in user and the user whose profile is being viewed.
    private String profileToViewUsername;
    private String loggedInUsername;

    // Base URL for the profiles API endpoint.
    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/profiles/";

    /**
     * Called when the activity is first created. Initializes UI components, determines if the
     * profile is the user's own or another's, and fetches the profile data.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Enable the back button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize all UI components.
        nameEditText = findViewById(R.id.profile_name_edt);
        songEditText = findViewById(R.id.profile_song_edt);
        genreEditText = findViewById(R.id.profile_genre_edt);
        artistEditText = findViewById(R.id.profile_artist_edt);
        bioEditText = findViewById(R.id.profile_bio_edt);
        profileTitleTextView = findViewById(R.id.profile_title_txt);
        updateButton = findViewById(R.id.profile_update_btn);
        deleteButton = findViewById(R.id.profile_delete_btn);
        logoutButton = findViewById(R.id.profile_logout_btn);
        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        // Get usernames from the intent.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOGGED_IN_USERNAME") && intent.hasExtra("PROFILE_TO_VIEW")) {
            loggedInUsername = intent.getStringExtra("LOGGED_IN_USERNAME");
            profileToViewUsername = intent.getStringExtra("PROFILE_TO_VIEW");

            // Set the profile title dynamically.
            if (loggedInUsername.equals(profileToViewUsername)) {
                profileTitleTextView.setText("Edit Your Profile");
            } else {
                profileTitleTextView.setText(profileToViewUsername + "\'s Profile");
            }

            // Set the action bar title.
            if(getSupportActionBar() != null) {
                getSupportActionBar().setTitle(profileToViewUsername + "\'s Profile");
            }

            fetchUserData();
            setupNavigation();

            // If viewing another user's profile, make it read-only.
            if (!loggedInUsername.equals(profileToViewUsername)) {
                makeProfileReadOnly();
            }

        } else {
            Toast.makeText(this, "Error: No user profile found.", Toast.LENGTH_LONG).show();
            finish(); // End the activity if no user data is provided.
        }

        // Set listeners for update, delete, and logout buttons.
        updateButton.setOnClickListener(v -> updateUserData());
        deleteButton.setOnClickListener(v -> deleteUserAccount());
        logoutButton.setOnClickListener(v -> {
            Intent logoutIntent = new Intent(ProfileActivity.this, LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            finish();
        });
    }

    /**
     * Handles the action bar's up button. Finishes the current activity.
     * @return True if the action was handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Disables editing fields and hides action buttons for a read-only view of a profile.
     */
    private void makeProfileReadOnly() {
        nameEditText.setEnabled(false);
        songEditText.setEnabled(false);
        genreEditText.setEnabled(false);
        artistEditText.setEnabled(false);
        bioEditText.setEnabled(false);

        updateButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);

        // Ensure navigation buttons are still visible.
        homeButton.setVisibility(View.VISIBLE);
        musicButton.setVisibility(View.VISIBLE);
        createButton.setVisibility(View.VISIBLE);
        jamsButton.setVisibility(View.VISIBLE);
        profileButton.setVisibility(View.VISIBLE);
    }

    /**
     * Fetches the profile data for the user from the server.
     */
    private void fetchUserData() {
        String url = BASE_URL + profileToViewUsername;
        Log.d("ProfileFetch", "Fetching data from URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Populate the EditText fields with the fetched data.
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

    /**
     * Sends a PUT request to the server to update the user's profile data.
     */
    private void updateUserData() {
        String url = BASE_URL + profileToViewUsername;
        JSONObject profileData = new JSONObject();
        try {
            // Create a JSON object with the updated profile information.
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

    /**
     * Sends a DELETE request to the server to delete the user's account.
     */
    private void deleteUserAccount() {
        String url = BASE_URL + profileToViewUsername;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_LONG).show();
                    // Redirect to login screen after account deletion.
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

    /**
     * Sets up the navigation buttons at the bottom of the screen.
     */
    private void setupNavigation() {
        homeButton.setOnClickListener(view -> navigateTo(HomeActivity.class));
        musicButton.setOnClickListener(view -> navigateTo(MusicActivity.class));
        createButton.setOnClickListener(view -> navigateTo(CreateActivity.class));
        jamsButton.setOnClickListener(view -> navigateTo(JamsActivity.class));

        profileButton.setOnClickListener(view -> {
            // If already on own profile, do nothing. Otherwise, navigate to own profile.
            if (loggedInUsername.equals(profileToViewUsername)) {
                return;
            }
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            intent.putExtra("PROFILE_TO_VIEW", loggedInUsername);
            startActivity(intent);
        });
    }

    /**
     * Helper method to navigate to another activity, passing the logged-in username.
     * @param activityClass The class of the activity to navigate to.
     */
    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(ProfileActivity.this, activityClass);
        intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
        startActivity(intent);
    }
}