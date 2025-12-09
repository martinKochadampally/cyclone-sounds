package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    private TextView titleText;
    private TextView artistText;
    private ImageView albumCoverImage;
    private ListView songsListView;
    private ArrayAdapter<String> songsAdapter;
    private ArrayList<String> songList;
    private RequestQueue requestQueue;
    private String loggedInUsername;

    private Button homeButton;
    private Button musicButton;
    private Button jamsButton;
    private Button createButton;
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        loggedInUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");

        titleText = findViewById(R.id.album_title_txt);
        artistText = findViewById(R.id.album_artist_txt);
        albumCoverImage = findViewById(R.id.album_cover_image);
        songsListView = findViewById(R.id.album_songs_list);

        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        setupNavigation();

        requestQueue = Volley.newRequestQueue(this);
        songList = new ArrayList<>();
        songsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songList);
        songsListView.setAdapter(songsAdapter);

        int albumId = getIntent().getIntExtra("ALBUM_ID", -1);
        String albumName = getIntent().getStringExtra("ALBUM_NAME");

        if (albumName != null) {
            titleText.setText(albumName);
        }

        if (albumId != -1) {
            fetchAlbumDetails(albumId);
        } else {
            Toast.makeText(this, "Error: No Album ID provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNavigation() {
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumActivity.this, HomeActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        musicButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumActivity.this, MusicActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumActivity.this, CreateActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        jamsButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumActivity.this, JamsActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlbumActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            intent.putExtra("PROFILE_TO_VIEW", loggedInUsername);
            startActivity(intent);
        });
    }

    private void fetchAlbumDetails(int id) {
        String url = BASE_URL + "/albums/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String title = response.has("title") ? response.getString("title") : "Unknown Title";
                        String artist = response.has("artist") ? response.getString("artist") : "Unknown Artist";

                        titleText.setText(title);
                        artistText.setText(artist);

                        // NOTE: If you have a cover URL in your backend, you would load it here using Glide or Picasso.
                        // Example: String coverUrl = response.getString("coverUrl");

                        songList.clear();
                        if (response.has("songs")) {
                            JSONArray songsArray = response.getJSONArray("songs");
                            for (int i = 0; i < songsArray.length(); i++) {
                                JSONObject songObj = songsArray.getJSONObject(i);
                                String songName = songObj.has("songName") ? songObj.getString("songName") : "Unknown Song";
                                songList.add(songName);
                            }
                        } else {
                            songList.add("No songs found for this album.");
                        }
                        songsAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("AlbumActivity", "JSON Parsing Error", e);
                        Toast.makeText(AlbumActivity.this, "Error parsing album details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("AlbumActivity", "Volley Error: " + error.toString());
                    Toast.makeText(AlbumActivity.this, "Failed to load album", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}