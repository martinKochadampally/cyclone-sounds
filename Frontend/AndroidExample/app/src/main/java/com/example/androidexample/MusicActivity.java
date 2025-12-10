package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity for displaying music reviews. It fetches a list of reviews from the server
 * and displays them in a table. Users can upvote or downvote reviews, and users who
 * created a review (or admins) can delete them.
 */
public class MusicActivity extends AppCompatActivity {

    // UI Elements for navigation and data display.
    private Button profileButton;
    private Button homeButton;
    private Button jamsButton;
    private Button createButton;
    private TableLayout tableLayout;

    // API endpoints for reviews and user credentials.
    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/reviews";
    private static final String UP_VOTE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/review/upvote/";
    private static final String DOWN_VOTE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/review/downvote/";
    private static final String DELETE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/review/";
    private final String URL_GET_ACCOUNT_TYPE = "http://coms-3090-008.class.las.iastate.edu:8080/credentials/";

    // Flag to check if the current user is an admin.
    private boolean isAdmin = false;

    // Callback interface to handle asynchronous fetching of account type.
    interface AccountTypeCallback {
        void onComplete();
    }

    /**
     * Called when the activity is first created. Initializes UI components, fetches user
     * account type, and then fetches music review data.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Initialize UI components.
        homeButton = findViewById(R.id.home_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        tableLayout = findViewById(R.id.song_table);

        // Get the logged-in username from the intent.
        Bundle extras = getIntent().getExtras();
        String username = "";
        if (extras != null) {
            username = extras.getString("LOGGED_IN_USERNAME");
        }

        // Asynchronously get the account type, then fetch music data.
        String finalUsername = username;
        getAccountType(finalUsername, () -> {
            // This callback code runs after the account type is fetched.
            fetchMusicData(finalUsername);
        });

        // Set up navigation button listeners.
        homeButton.setOnClickListener(view -> navigateTo(HomeActivity.class, finalUsername));
        profileButton.setOnClickListener(view -> navigateTo(ProfileActivity.class, finalUsername));
        jamsButton.setOnClickListener(view -> navigateTo(JamsActivity.class, finalUsername));
        createButton.setOnClickListener(view -> navigateTo(CreateActivity.class, finalUsername));
    }

    /**
     * Fetches music review data from the server and populates the table.
     * @param username The currently logged-in user.
     */
    private void fetchMusicData(final String username) {
        clearTable();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, URL_STRING_REQ, null,
                response -> {
                    Log.d("Volley Response", "Received " + response.length() + " reviews.");
                    try {
                        // Loop through each review in the JSON response array.
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject reviewObject = response.getJSONObject(i);

                            // Extract review details.
                            String user = reviewObject.optString("reviewer", "N/A");
                            int rating = reviewObject.optInt("rating", 0);
                            int upvotes = reviewObject.optInt("upVotes", 0);
                            int downvotes = reviewObject.optInt("downVotes", 0);
                            int reviewId = reviewObject.optInt("id", -1);

                            // Extract song details from the nested JSON object.
                            JSONObject songObject = reviewObject.getJSONObject("song");
                            String title = songObject.optString("songName", "N/A");
                            String artist = songObject.optString("artist", "N/A");

                            // Create a new row and add TextViews for each piece of data.
                            TableRow tableRow = new TableRow(MusicActivity.this);
                            tableRow.addView(createTextView(user, true));
                            tableRow.addView(createTextView(title, true));
                            tableRow.addView(createTextView(artist, true));
                            tableRow.addView(createTextView(String.valueOf(rating), true));
                            tableRow.addView(createTextView(String.valueOf(upvotes), true));
                            tableRow.addView(createTextView(String.valueOf(downvotes), true));

                            // Add upvote and downvote buttons.
                            Button upvoteButton = createButton("Up", String.valueOf(reviewId), "upvote", username);
                            Button downvoteButton = createButton("Down", String.valueOf(reviewId), "downvote", username);
                            tableRow.addView(upvoteButton);
                            tableRow.addView(downvoteButton);

                            // Add a delete button if the user is the reviewer or an admin.
                            if (user.equals(username) || isAdmin) {
                                Button deleteButton = createDeleteButton("Del", String.valueOf(reviewId), username);
                                tableRow.addView(deleteButton);
                            } else {
                                // Add an empty view to maintain table structure.
                                tableRow.addView(new View(MusicActivity.this));
                            }
                            tableLayout.addView(tableRow);
                        }
                    } catch (JSONException e) {
                        Log.e("Volley JSON Error", "Error parsing JSON array: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", "Error fetching reviews: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Could not load reviews", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Helper method to create a TextView for the table.
     * @param text The text to display.
     * @param useWeight Whether to apply a layout weight for even column distribution.
     * @return The created TextView.
     */
    private TextView createTextView(String text, boolean useWeight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        if (useWeight) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(params);
        }
        return textView;
    }

    /**
     * Fetches the account type of the logged-in user to determine admin privileges.
     * @param username The username to check.
     * @param callback A callback to execute after the network request is complete.
     */
    private void getAccountType(final String username, AccountTypeCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_GET_ACCOUNT_TYPE + username, null,
                response -> {
                    try {
                        String accountType = response.getString("accountType");
                        Log.d("MusicActivity", "Account Type: " + accountType);
                        isAdmin = accountType.equals("admin");
                        callback.onComplete(); // Trigger the callback.
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                        callback.onComplete(); // Also trigger callback on error to not block UI.
                    }
                },
                error -> {
                    Log.e("JamsActivity", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Could not load account type", Toast.LENGTH_LONG).show();
                    callback.onComplete(); // Also trigger callback on error.
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Creates a vote button (Up or Down).
     * @param text The button text ("Up" or "Down").
     * @param reviewId The ID of the review to vote on.
     * @param voteType The type of vote ("upvote" or "downvote").
     * @param username The current user.
     * @return The created Button.
     */
    private Button createButton(String text, final String reviewId, final String voteType, final String username) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(11);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.width = 100; // Fixed width for the button.
        button.setLayoutParams(params);
        button.setOnClickListener(view -> {
            String url = voteType.equals("upvote") ? UP_VOTE_URL : DOWN_VOTE_URL;
            voteForSong(reviewId, username, url);
        });
        return button;
    }

    /**
     * Creates a delete button for a review.
     * @param text The button text ("Del").
     * @param reviewId The ID of the review to delete.
     * @param username The current user.
     * @return The created Button.
     */
    private Button createDeleteButton(String text, final String reviewId, final String username) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(10);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.width = 110; // Fixed width for the button.
        button.setLayoutParams(params);
        button.setOnClickListener(view -> deleteRating(reviewId, username));
        return button;
    }

    /**
     * Sends a request to the server to delete a specific review.
     * @param reviewId The ID of the review to delete.
     * @param username The current user (for refreshing the list).
     */
    private void deleteRating(final String reviewId, final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, DELETE_URL + reviewId,
                response -> {
                    Log.d("Delete Response", response);
                    Toast.makeText(MusicActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    fetchMusicData(username); // Refresh the data.
                },
                error -> {
                    Log.e("Delete Error", error.toString());
                    Toast.makeText(MusicActivity.this, "Delete Error", Toast.LENGTH_SHORT).show();
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Sends a request to the server to cast a vote on a review.
     * @param reviewId The ID of the review to vote on.
     * @param username The current user (for refreshing the list).
     * @param url The API endpoint for upvoting or downvoting.
     */
    private void voteForSong(final String reviewId, final String username, String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url + reviewId,
                response -> {
                    Log.d("Vote Response", response);
                    Toast.makeText(MusicActivity.this, "Voted", Toast.LENGTH_SHORT).show();
                    fetchMusicData(username); // Refresh the data.
                },
                error -> {
                    Log.e("Vote Error", error.toString());
                    Toast.makeText(MusicActivity.this, "Vote Error", Toast.LENGTH_SHORT).show();
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Clears all rows from the table except the header row.
     */
    private void clearTable() {
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }
    }

    /**
     * Helper method to navigate to another activity.
     * @param activityClass The class of the activity to navigate to.
     * @param username The username to pass to the next activity.
     */
    private void navigateTo(Class<?> activityClass, String username) {
        Intent intent = new Intent(MusicActivity.this, activityClass);
        intent.putExtra("LOGGED_IN_USERNAME", username);
        intent.putExtra("PROFILE_TO_VIEW", username); // Often, the default profile to view is the user's own.
        startActivity(intent);
    }
}
