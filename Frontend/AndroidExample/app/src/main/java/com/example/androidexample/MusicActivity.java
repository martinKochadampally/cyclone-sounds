package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public class MusicActivity extends AppCompatActivity {

    private Button profileButton;
    private Button homeButton;
    private Button jamsButton;
    private Button createButton;

    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/reviews";
    private static final String UP_VOTE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/review/upvote/";
    private static final String DOWN_VOTE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/review/downvote/";
    private static final String DELETE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/review/";
    private final String URL_GET_ACCOUNT_TYPE = "http://coms-3090-008.class.las.iastate.edu:8080/credentials/";
    private boolean isAdmin = false;

    interface AccountTypeCallback {
        void onComplete();
    }
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        homeButton = findViewById(R.id.home_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            currentUsername = intent.getStringExtra("USERNAME");
        }

        String finalUsername = username;
        getAccountType(finalUsername, () -> {
            // This code will run after the network request is complete
            fetchMusicData(finalUsername);
        });


        homeButton.setOnClickListener(view -> navigateTo(HomeActivity.class, finalUsername));
        profileButton.setOnClickListener(view -> navigateTo(ProfileActivity.class, finalUsername));
        jamsButton.setOnClickListener(view -> navigateTo(JamsActivity.class, finalUsername));
        createButton.setOnClickListener(view -> navigateTo(CreateActivity.class, finalUsername));
    }

    private void fetchMusicData(final String username) {
        clearTable();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_STRING_REQ,
                null,
                response -> {
                    Log.d("Volley Response", "Received " + response.length() + " reviews.");
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject reviewObject = response.getJSONObject(i);

                            String user = reviewObject.optString("reviewer", "N/A");
                            int rating = reviewObject.optInt("rating", 0);
                            int upvotes = reviewObject.optInt("upVotes", 0);
                            int downvotes = reviewObject.optInt("downVotes", 0);
                            int reviewId = reviewObject.optInt("id", -1);

                            JSONObject songObject = reviewObject.getJSONObject("song");
                            String title = songObject.optString("songName", "N/A");
                            String artist = songObject.optString("artist", "N/A");

                            TableRow tableRow = new TableRow(MusicActivity.this);
                            tableRow.addView(createTextView(user, true));
                            tableRow.addView(createTextView(title, true));
                            tableRow.addView(createTextView(artist, true));
                            tableRow.addView(createTextView(String.valueOf(rating), true));
                            tableRow.addView(createTextView(String.valueOf(upvotes), true));
                            tableRow.addView(createTextView(String.valueOf(downvotes), true));

                            Button upvoteButton = createButton("Up", String.valueOf(reviewId), "upvote", username);
                            Button downvoteButton = createButton("Down", String.valueOf(reviewId), "downvote", username);
                            tableRow.addView(upvoteButton);
                            tableRow.addView(downvoteButton);
                            if (user.equals(username) || isAdmin) {
                                Button deleteButton = createDeleteButton("Del", String.valueOf(reviewId), username);
                                tableRow.addView(deleteButton);
                            } else {
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

    private TextView createTextView(String text, boolean useWeight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        if (useWeight) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(params);
        }
        return textView;
    }

    private void getAccountType(final String username, AccountTypeCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_GET_ACCOUNT_TYPE + username,
                null,
                response -> {
                    try {
                        String accountType = response.getString("accountType");
                        Log.d("MusicActivity", "Account Type: " + accountType);
                        if (accountType.equals("admin")) {
                            isAdmin = true;
                            callback.onComplete();
                        } else {
                            callback.onComplete();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("JamsActivity", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Could not load account type", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    private Button createButton(String text, final String reviewId, final String voteType, final String username) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(11);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.width = 100;
        button.setLayoutParams(params);
        button.setOnClickListener(view -> {
            if (voteType.equals("upvote")) {
                voteForSong(reviewId, username, UP_VOTE_URL);
            } else {
                voteForSong(reviewId, username, DOWN_VOTE_URL);
            }
=======
        homeButton.setOnClickListener(view -> {
            Intent homeIntent = new Intent(MusicActivity.this, HomeActivity.class);
            homeIntent.putExtra("USERNAME", currentUsername);
            startActivity(homeIntent);
>>>>>>> Frontend/AndroidExample/app/src/main/java/com/example/androidexample/MusicActivity.java
        });

        profileButton.setOnClickListener(view -> {
            Intent profileIntent = new Intent(MusicActivity.this, ProfileActivity.class);
            profileIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            profileIntent.putExtra("PROFILE_TO_VIEW", currentUsername);
            startActivity(profileIntent);
        });

        jamsButton.setOnClickListener(view -> {
            Intent jamsIntent = new Intent(MusicActivity.this, JamsActivity.class);
            jamsIntent.putExtra("USERNAME", currentUsername);
            startActivity(jamsIntent);
        });

        createButton.setOnClickListener(view -> {
            Intent createIntent = new Intent(MusicActivity.this, CreateActivity.class);
            createIntent.putExtra("USERNAME", currentUsername);
            startActivity(createIntent);
        });
    }
}