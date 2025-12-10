package com.example.androidexample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private ListView songsListView;
    private ArrayAdapter<String> songsAdapter;

    private ArrayList<String> songList;
    private ArrayList<String> songUrls;

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
        songsListView = findViewById(R.id.album_songs_list);

        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        setupNavigation();

        requestQueue = Volley.newRequestQueue(this);
        songList = new ArrayList<>();
        songUrls = new ArrayList<>();

        songsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songList);
        songsListView.setAdapter(songsAdapter);

        songsListView.setOnItemClickListener((parent, view, position, id) -> {
            String url = songUrls.get(position);
            String name = songList.get(position);

            if (url != null && !url.isEmpty()) {
                showSpotifyDialog(url, name);
            } else {
                Toast.makeText(AlbumActivity.this, "No preview available for this song.", Toast.LENGTH_SHORT).show();
            }
        });

        String spotifyId = getIntent().getStringExtra("SPOTIFY_ALBUM_ID");
        int localId = getIntent().getIntExtra("ALBUM_ID", -1);
        String albumName = getIntent().getStringExtra("ALBUM_NAME");

        if (albumName != null) {
            titleText.setText(albumName);
        }

        if (spotifyId != null) {
            fetchAlbumWithUrl(BASE_URL + "/albums/" + spotifyId);
        } else if (localId != -1) {
            fetchAlbumWithUrl(BASE_URL + "/albums/database/" + localId);
        } else {
            Toast.makeText(this, "Error: No Album ID provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAlbumWithUrl(String url) {
        Log.d("AlbumActivity", "Fetching URL: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String title = response.optString("title", "Unknown Title");
                        String artist = response.optString("artist", "Unknown Artist");
                        titleText.setText(title);
                        artistText.setText(artist);

                        songList.clear();
                        songUrls.clear();

                        if (response.has("songs")) {
                            JSONArray songsArray = response.getJSONArray("songs");
                            for (int i = 0; i < songsArray.length(); i++) {
                                JSONObject songObj = songsArray.getJSONObject(i);
                                String songName = songObj.optString("songName", "Unknown Song");

                                String finalUrl = songObj.optString("embedURL", "");
                                if (finalUrl.isEmpty()) finalUrl = songObj.optString("embedUrl", "");

                                if (finalUrl.isEmpty()) {
                                    String spotifyId = songObj.optString("spotifyId", "");
                                    if (!spotifyId.isEmpty()) {
                                        finalUrl = "https://open.spotify.com/embed/track/" + spotifyId;
                                    }
                                }
                                songList.add(songName);
                                songUrls.add(finalUrl);
                            }
                        } else {
                            songList.add("No songs found for this album.");
                            songUrls.add("");
                        }
                        songsAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("AlbumActivity", "JSON Parsing Error", e);
                        Toast.makeText(AlbumActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMsg = "Failed to load album";
                    if (error.networkResponse != null) {
                        errorMsg += " (Code " + error.networkResponse.statusCode + ")";
                    }
                    Log.e("AlbumActivity", "Volley Error: " + error.toString());
                    Toast.makeText(AlbumActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void showSpotifyDialog(String embedUrl, String songName) {
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.loadUrl(embedUrl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Now Playing: " + songName);
        builder.setView(webView);

        builder.setNegativeButton("Close Player", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                webView.destroy();
                dialog.dismiss();
            }
        });

        builder.setOnDismissListener(dialog -> webView.destroy());

        builder.show();
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
}