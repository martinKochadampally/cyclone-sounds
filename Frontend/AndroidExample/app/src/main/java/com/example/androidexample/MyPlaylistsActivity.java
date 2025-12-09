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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This activity displays a list of the current user's playlists.
 * It allows them to view, edit (add/remove songs), and delete their playlists.
 */
public class MyPlaylistsActivity extends AppCompatActivity {
    // UI Elements
    private Button backButton;
    private TableLayout tableLayout;
    // Data fields
    private String currentUsername;

    // API Endpoint URL
    private static final String PLAYLISTS_URL = "http://coms-3090-008.class.las.iastate.edu:8080/api/playlists/";

    /**
     * Called when the activity is first created. Initializes UI, retrieves the user's
     * information, and fetches their playlists from the server.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_playlists);

        // Initialize UI components from the layout.
        backButton = findViewById(R.id.back_btn);
        tableLayout = findViewById(R.id.playlists_table);

        // Get the username passed from the previous activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("LOGGED_IN_USERNAME");
        }

        // Fetch the user's playlists.
        fetchPlaylists(currentUsername);

        // Set listener for the back button to navigate to the home screen.
        backButton.setOnClickListener(view -> navigateTo(HomeActivity.class, currentUsername));
    }

    /**
     * Fetches the playlists owned by the specified user from the server.
     * @param username The username of the playlist owner.
     */
    private void fetchPlaylists(final String username) {
        clearTable();
        String url = PLAYLISTS_URL + "owner/" + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("Volley Response", "Received " + response.length() + " playlists.");
                    try {
                        // Iterate through the JSON array of playlists.
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject playlistObject = response.getJSONObject(i);
                            String playlistName = playlistObject.optString("playlistName", "N/A");
                            JSONArray songs = playlistObject.optJSONArray("songs");
                            int songCount = (songs != null) ? songs.length() : 0;

                            // Create a new row for each playlist and add it to the table.
                            TableRow tableRow = new TableRow(MyPlaylistsActivity.this);
                            tableRow.addView(createTextView(playlistName));
                            tableRow.addView(createTextView(String.valueOf(songCount)));
                            tableRow.addView(createEditButton(playlistName, username));
                            tableRow.addView(createDeleteButton(playlistName, username));
                            tableLayout.addView(tableRow);
                        }
                    } catch (JSONException e) {
                        Log.e("Volley JSON Error", "Error parsing JSON array: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", "Error fetching playlists: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Could not load playlists", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Creates a simple TextView for use in table cells.
     * @param text The text to display in the TextView.
     * @return The created TextView.
     */
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    /**
     * Creates an "Edit" button for a playlist row.
     * Clicking it navigates to the AddSongsActivity to edit the playlist.
     * @param playlistName The name of the playlist to edit.
     * @param username The current user's username.
     * @return The created Button.
     */
    private Button createEditButton(final String playlistName, final String username) {
        Button button = new Button(this);
        button.setText("Edit");
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MyPlaylistsActivity.this, AddSongsActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", username);
            intent.putExtra("PLAYLIST_NAME", playlistName);
            intent.putExtra("PREVIOUS_PAGE", "MY_PLAYLISTS");
            startActivity(intent);
        });
        return button;
    }

    /**
     * Creates a "Delete" button for a playlist row.
     * Clicking it will trigger the deletion of the playlist.
     * @param playlistName The name of the playlist to delete.
     * @param username The current user's username.
     * @return The created Button.
     */
    private Button createDeleteButton(final String playlistName, final String username) {
        Button button = new Button(this);
        button.setText("Delete");
        button.setOnClickListener(view -> deletePlaylist(playlistName, username));
        return button;
    }

    /**
     * Sends a DELETE request to the server to delete a specified playlist.
     * @param playlistName The name of the playlist to be deleted.
     * @param username The owner of the playlist.
     */
    private void deletePlaylist(final String playlistName, final String username) {
        String url = PLAYLISTS_URL + username + "/" + playlistName;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("Delete Response", response);
                    Toast.makeText(MyPlaylistsActivity.this, "Playlist deleted", Toast.LENGTH_SHORT).show();
                    fetchPlaylists(username); // Refresh the list after deletion.
                },
                error -> {
                    Log.e("Delete Error", error.toString());
                    Toast.makeText(MyPlaylistsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Clears all rows from the playlists table, except for the header row.
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
        Intent intent = new Intent(MyPlaylistsActivity.this, activityClass);
        intent.putExtra("LOGGED_IN_USERNAME", username);
        startActivity(intent);
    }
}
