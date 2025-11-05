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

public class MyPlaylistsActivity extends AppCompatActivity {
    private Button backButton;
    private TableLayout tableLayout;
    private String currentUsername;

    private static final String PLAYLISTS_URL = "http://coms-3090-008.class.las.iastate.edu:8080/playlists/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_playlists);

        backButton = findViewById(R.id.back_btn);
        tableLayout = findViewById(R.id.playlists_table);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("USERNAME");
        }

        fetchPlaylists(currentUsername);

        backButton.setOnClickListener(view -> navigateTo(CreateActivity.class, currentUsername));
    }

    private void fetchPlaylists(final String username) {
        clearTable();
        String url = PLAYLISTS_URL + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url, 
                null,
                response -> {
                    Log.d("Volley Response", "Received " + response.length() + " playlists.");
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject playlistObject = response.getJSONObject(i);
                            String playlistName = playlistObject.optString("name", "N/A");
                            JSONArray songs = playlistObject.optJSONArray("songs");
                            int songCount = (songs != null) ? songs.length() : 0;

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

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private Button createEditButton(final String playlistName, final String username) {
        Button button = new Button(this);
        button.setText("Edit");
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MyPlaylistsActivity.this, AddSongsActivity.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("PLAYLIST_NAME", playlistName);
            startActivity(intent);
        });
        return button;
    }

    private Button createDeleteButton(final String playlistName, final String username) {
        Button button = new Button(this);
        button.setText("Delete");
        button.setOnClickListener(view -> deletePlaylist(playlistName, username));
        return button;
    }

    private void deletePlaylist(final String playlistName, final String username) {
        String url = PLAYLISTS_URL + username + "/" + playlistName;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("Delete Response", response);
                    Toast.makeText(MyPlaylistsActivity.this, "Playlist deleted", Toast.LENGTH_SHORT).show();
                    fetchPlaylists(username); // Refresh the list
                },
                error -> {
                    Log.e("Delete Error", error.toString());
                    Toast.makeText(MyPlaylistsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void clearTable() {
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }
    }

    private void navigateTo(Class<?> activityClass, String username) {
        Intent intent = new Intent(MyPlaylistsActivity.this, activityClass);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}
