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
 * This activity provides a form for users to create and submit a review for a song.
 */
public class CreateReviewActivity extends AppCompatActivity {

    // UI Elements for the review form
    private Button backButton;
    private EditText songName;
    private EditText artistName;
    private EditText rating;
    private EditText description;
    private Button submitButton;

    // Data fields
    private String currentUsername;

    // API Endpoint URL
    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/review";

    /**
     * Called when the activity is first created. Initializes the UI, retrieves user data,
     * and sets up listeners for form submission and navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);

        // Initialize UI components from the layout.
        backButton = findViewById(R.id.back_btn);
        songName = findViewById(R.id.song_name_edt);
        artistName = findViewById(R.id.arist_name_edt);
        rating = findViewById(R.id.rating_edt);
        description = findViewById(R.id.description_edt);
        submitButton = findViewById(R.id.submit_btn);

        // Get the username passed from the previous activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("LOGGED_IN_USERNAME");
        }

        // Set listener for the back button to return to the CreateActivity.
        backButton.setOnClickListener(view -> navigateTo(CreateActivity.class));

        // Set listener for the submit button to validate and post the review.
        submitButton.setOnClickListener(view -> {
            String song = songName.getText().toString().trim();
            String artist = artistName.getText().toString().trim();
            String rate = rating.getText().toString().trim();
            String desc = description.getText().toString().trim();

            // Validate required fields.
            if (song.isEmpty() || artist.isEmpty() || rate.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill in all required fields", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                // Validate the rating value.
                double ratingValue = Double.parseDouble(rate);
                if (ratingValue > 5 || ratingValue < 0) {
                    Toast.makeText(getApplicationContext(), "Rating must be between 0 and 5", Toast.LENGTH_LONG).show();
                    return;
                }
                // If validation passes, make the post request.
                makePostRequest(currentUsername, song, artist, rate, desc);
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Please enter a valid number for rating", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Makes a POST request to the server to submit the new review.
     *
     * @param user        The username of the reviewer.
     * @param songName    The name of the song being reviewed.
     * @param artistName  The name of the artist.
     * @param rating      The rating given to the song (as a string).
     * @param description The text content of the review.
     */
    private void makePostRequest(final String user, final String songName, final String artistName, final String rating, final String description) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ,
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(getApplicationContext(), "Review Submitted Successfully!", Toast.LENGTH_LONG).show();

                    // On success, navigate to the MusicActivity to see the new review.
                    Intent intent = new Intent(CreateReviewActivity.this, MusicActivity.class);
                    intent.putExtra("LOGGED_IN_USERNAME", user);
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

    /**
     * Helper method to navigate to another activity, passing the current username.
     * @param activityClass The class of the activity to navigate to.
     */
    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(CreateReviewActivity.this, activityClass);
        intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
        startActivity(intent);
    }
}
