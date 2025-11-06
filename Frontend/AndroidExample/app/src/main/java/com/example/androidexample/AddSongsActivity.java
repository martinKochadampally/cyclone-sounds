package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddSongsActivity extends AppCompatActivity {

    private TextView playlistNameTextView;
    private Button backButton;
    private Button saveButton;
    private Button searchButton;
    private EditText songSearchEditText;
    private TableLayout songSearchTable;
    private TableLayout playlistSongsTable;
    private String currentUsername;
    private String currentPlaylistName;

    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";
    private static final String PLAYLISTS_URL = BASE_URL + "/api/playlists/";
    private static final String SONGS_URL = BASE_URL + "/search/songs/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_songs);

        playlistNameTextView = findViewById(R.id.playlist_name_txt);
        backButton = findViewById(R.id.back_btn);
        saveButton = findViewById(R.id.save_btn);
        searchButton = findViewById(R.id.search_button_btn);
        songSearchEditText = findViewById(R.id.song_search_edt);
        songSearchTable = findViewById(R.id.song_search_table);
        playlistSongsTable = findViewById(R.id.playlist_songs_table);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("LOGGED_IN_USERNAME");
            currentPlaylistName = extras.getString("PLAYLIST_NAME");
            playlistNameTextView.setText(currentPlaylistName);
        }

        // Fetch and display songs already in the playlist
        getPlaylistSongs();

        searchButton.setOnClickListener(view -> {
            String query = songSearchEditText.getText().toString();
            if (!query.isEmpty()) {
                searchSongs(query);
            } else {
                Toast.makeText(this, "Please enter a song name to search", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(view -> {
            if (extras.getString("PREVIOUS_PAGE").equals("CREATE")) {
                Intent intent = new Intent(AddSongsActivity.this, CreateActivity.class);
                intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AddSongsActivity.this, MyPlaylistsActivity.class);
                intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(view -> {
            // The save button just goes back, maybe it should do more? For now, this is what it did.
            if (extras.getString("PREVIOUS_PAGE").equals("CREATE")) {
                Intent intent = new Intent(AddSongsActivity.this, CreateActivity.class);
                intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AddSongsActivity.this, MyPlaylistsActivity.class);
                intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
                startActivity(intent);
            }
        });
    }

    /**
     * Fetches the list of songs in the current playlist from the server.
     */
    private void getPlaylistSongs() {
        String url = PLAYLISTS_URL + currentUsername + "/" + currentPlaylistName + "/songs";

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        populatePlaylistTable(response);
                    } catch (JSONException e) {
                        Log.e("Volley JSON Error", "Error parsing playlist songs: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Error parsing playlist", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", "Error fetching playlist: " + error.toString());
                    // It might be a new playlist, so an error (like 404) is okay.
                    // Clear the table in case it had old data.
                    clearTable(playlistSongsTable);
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Populates the playlist songs table with data from the server.
     *
     * @param songs A JSONArray of song objects.
     */
    private void populatePlaylistTable(JSONArray songs) throws JSONException {
        clearTable(playlistSongsTable);
        for (int i = 0; i < songs.length(); i++) {
            JSONObject song = songs.getJSONObject(i);
            String songName = song.optString("songName", "N/A");
            String artist = song.optString("artist", "N/A");

            TableRow tableRow = new TableRow(this);
            tableRow.addView(createTextView(songName));
            tableRow.addView(createTextView(artist));
            tableRow.addView(createRemoveButton(songName, artist));
            playlistSongsTable.addView(tableRow);
        }
    }

    /**
     * Searches for songs based on a query.
     *
     * @param query The song name to search for.
     */
    private void searchSongs(String query) {
        String url = SONGS_URL + query; // Assuming endpoint is /search/songs/{name}

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                this::populateSearchTable,
                error -> {
                    Log.e("Volley Error", "Error searching songs: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Song not found", Toast.LENGTH_SHORT).show();
                    clearTable(songSearchTable);
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Populates the search results table with songs.
     *
     * @param songs A JSONArray of song objects from the search.
     */
    private void populateSearchTable(JSONArray songs) {
        clearTable(songSearchTable);
        try {
            for (int i = 0; i < songs.length(); i++) {
                JSONObject song = songs.getJSONObject(i);
                String songName = song.optString("songName", "N/A");
                String artist = song.optString("artist", "N/A");

                TableRow tableRow = new TableRow(this);
                tableRow.addView(createTextView(songName));
                tableRow.addView(createTextView(artist));
                tableRow.addView(createAddButton(songName, artist));

                songSearchTable.addView(tableRow);
            }
        } catch (JSONException e) {
            Log.e("Volley JSON Error", "Error parsing search results: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Error parsing search results", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sends a request to add a song to the current playlist.
     * On success, it refreshes the playlist table.
     *
     * @param songName The name of the song to add.
     */
    private void addSongToPlaylist(final String songName, final String artist) {
        String url = PLAYLISTS_URL + currentUsername + "/" + currentPlaylistName + "/add";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Volley Response", "Add song: " + response);
                    Toast.makeText(getApplicationContext(), "Song added", Toast.LENGTH_SHORT).show();
                    // Refresh the playlist to show the newly added song
                    getPlaylistSongs();
                },
                error -> {
                    Log.e("Volley Error", "Error adding song: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Failed to add song", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("songName", songName);
                params.put("artist", artist);

                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Sends a request to remove a song from the current playlist.
     * On success, it refreshes the playlist table.
     *
     * @param songName The name of the song to remove.
     */
    private void removeSongFromPlaylist(final String songName, final String artist) {
        String url = PLAYLISTS_URL + currentUsername + "/" + currentPlaylistName +
                "/remove?songName=" + Uri.encode(songName) +
                "&artist=" + Uri.encode(artist);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("Volley Response", "Remove song: " + response);
                    Toast.makeText(getApplicationContext(), "Song removed", Toast.LENGTH_SHORT).show();
                    getPlaylistSongs();
                },
                error -> {
                    Log.e("Volley Error", "Error removing song: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Failed to remove song", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("songName", songName);
                params.put("artist", artist);
                return params;
            }
        };


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Creates a simple TextView for table cells.
     */
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setWidth(20);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    /**
     * Creates an "Add" button for the search results table.
     */
    private Button createAddButton(final String songName, final String artist) {
        Button button = new Button(this);
        button.setText("Add");
        button.setOnClickListener(v -> addSongToPlaylist(songName, artist));
        return button;
    }

    /**
     * Creates a "Remove" button for the playlist table.
     */
    private Button createRemoveButton(final String songName, final String artist) {
        Button button = new Button(this);
        button.setText("Remove");
        button.setOnClickListener(v -> removeSongFromPlaylist(songName, artist));
        return button;
    }

    /**
     * Clears all rows from a TableLayout, except for the header row (at index 0).
     */
    private void clearTable(TableLayout table) {
        int childCount = table.getChildCount();
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(AddSongsActivity.this, activityClass);
        intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
        startActivity(intent);
    }
}