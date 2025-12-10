package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity allows a user to create a new, empty playlist by providing a name.
 */
public class CreatePlaylistActivity extends AppCompatActivity {

    // UI Elements
    private Button backButton;
    private EditText playlistName;
    private Button submitButton;

    // Data fields
    private String currentUsername;

    // API Endpoint URL
    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/api/playlists/";

    /**
     * Called when the activity is first created. Initializes UI components and sets up
     * listeners for form submission and navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        // Initialize UI components from the layout.
        backButton = findViewById(R.id.back_btn);
        playlistName = findViewById(R.id.playlist_name_edt);
        submitButton = findViewById(R.id.submit_btn);

        // Get the username passed from the previous activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("LOGGED_IN_USERNAME");
        }

        // Set listener for the back button to navigate to the CreateActivity.
        backButton.setOnClickListener(view -> navigateTo(CreateActivity.class));

        // Set listener for the submit button to validate and create the playlist.
        submitButton.setOnClickListener(view -> {
            String playlist = playlistName.getText().toString().trim();

            // Validate that the playlist name is not empty.
            if (playlist.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                // If validation passes, make the POST request.
                createPlaylistRequest(currentUsername, playlist);
            } catch (NumberFormatException e) {
                // This catch block is likely unnecessary as no number conversion is happening here.
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Makes a POST request to the server to create a new playlist.
     *
     * @param user         The username of the user creating the playlist.
     * @param playlistName The name for the new playlist.
     */
    private void createPlaylistRequest(final String user, final String playlistName) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ + "create",
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(this, "Playlist Created", Toast.LENGTH_SHORT).show();

                    // On success, navigate to the AddSongsActivity to add songs to the new playlist.
                    Intent intent = new Intent(CreatePlaylistActivity.this, AddSongsActivity.class);
                    intent.putExtra("LOGGED_IN_USERNAME", user);
                    intent.putExtra("PLAYLIST_NAME", playlistName);
                    intent.putExtra("PREVIOUS_PAGE", "CREATE");
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
                // Define the parameters to be sent in the POST request body.
                Map<String, String> params = new HashMap<>();
                params.put("playlistName", playlistName);
                params.put("username", user);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Helper method to navigate to another activity, passing the current username.
     * @param activityClass The class of the activity to navigate to.
     */
    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(CreatePlaylistActivity.this, activityClass);
        intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
        startActivity(intent);
    }
}
