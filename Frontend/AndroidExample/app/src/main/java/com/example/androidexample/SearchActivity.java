package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    private SearchView searchView;
    private Spinner searchTypeSpinner;
    private Button searchSubmitButton;
    private ListView profileResultsList;
    private ListView songResultsList;
    private TextView profilesHeader;
    private TextView songsHeader;

    private ArrayAdapter<String> profileAdapter;
    private ArrayAdapter<Song> songAdapter;

    private ArrayList<String> profileUsernames;
    private ArrayList<Song> songList;

    private RequestQueue requestQueue;
    private String loggedInUsername;
    private String currentSearchType;

    private static class Song {
        private final int songId;
        private final String songName;
        private final String artist;

        public Song(int songId, String songName, String artist) {
            this.songId = songId;
            this.songName = songName;
            this.artist = artist;
        }

        public int getSongId() {
            return songId;
        }

        @NonNull
        @Override
        public String toString() {
            return songName + " by " + artist;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search");
        }

        loggedInUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");
        requestQueue = Volley.newRequestQueue(this);

        searchView = findViewById(R.id.search_view);
        searchTypeSpinner = findViewById(R.id.search_type_spinner);
        searchSubmitButton = findViewById(R.id.search_submit_button);
        profileResultsList = findViewById(R.id.profile_results_list);
        songResultsList = findViewById(R.id.song_results_list);
        profilesHeader = findViewById(R.id.profiles_header);
        songsHeader = findViewById(R.id.songs_header);

        profileUsernames = new ArrayList<>();
        profileAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profileUsernames);
        profileResultsList.setAdapter(profileAdapter);

        songList = new ArrayList<>();
        songAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songList);
        songResultsList.setAdapter(songAdapter);

        setupSpinner();
        setupSearchView();
        setupButtonListener();
        setupClickListeners();
    }

    private void setupSpinner() {
        String[] searchTypes = {"Profiles", "Songs", "Playlists"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, searchTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(spinnerAdapter);

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSearchType = parent.getItemAtPosition(position).toString();
                updateVisibleList();
                searchView.setQuery("", false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        currentSearchType = searchTypes[0];
        updateVisibleList();
    }

    private void updateVisibleList() {
        clearResults();

        if (currentSearchType.equals("Profiles")) {
            profilesHeader.setVisibility(View.VISIBLE);
            profileResultsList.setVisibility(View.VISIBLE);
            songsHeader.setVisibility(View.GONE);
            songResultsList.setVisibility(View.GONE);
            searchView.setQueryHint("Search for users...");
        } else if (currentSearchType.equals("Songs")) {
            profilesHeader.setVisibility(View.GONE);
            profileResultsList.setVisibility(View.GONE);
            songsHeader.setVisibility(View.VISIBLE);
            songResultsList.setVisibility(View.VISIBLE);
            searchView.setQueryHint("Search for songs or artists...");
        } else if (currentSearchType.equals("Playlists")) {
            profilesHeader.setVisibility(View.GONE);
            profileResultsList.setVisibility(View.GONE);
            songsHeader.setVisibility(View.GONE);
            songResultsList.setVisibility(View.GONE);
            searchView.setQueryHint("Search for playlists...");
        }
    }

    private void setupSearchView() {
        // This line was fixed (removed extra dot)
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupButtonListener() {
        searchSubmitButton.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            if (query != null && !query.isEmpty()) {
                runSearch(query);
            }
            searchView.clearFocus();
        });
    }

    private void setupClickListeners() {
        profileResultsList.setOnItemClickListener((parent, view, position, id) -> {
            String profileToView = profileUsernames.get(position);
            incrementProfileViews(profileToView);

            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            intent.putExtra("PROFILE_TO_VIEW", profileToView);
            startActivity(intent);
        });

        songResultsList.setOnItemClickListener((parent, view, position, id) -> {
            Song clickedSong = songList.get(position);
            incrementSongSearches(clickedSong.getSongId());
            Toast.makeText(this, "Clicked on: " + clickedSong.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private void runSearch(String query) {
        if (currentSearchType.equals("Profiles")) {
            searchProfiles(query);
        } else if (currentSearchType.equals("Songs")) {
            searchSongs(query);
        } else if (currentSearchType.equals("Playlists")) {
            Toast.makeText(SearchActivity.this, "Playlist search not implemented yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearResults() {
        profileUsernames.clear();
        songList.clear();
        profileAdapter.notifyDataSetChanged();
        songAdapter.notifyDataSetChanged();
    }

    private void searchProfiles(String query) {
        String url = BASE_URL + "/search/profiles/" + query;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        profileUsernames.clear();
                        for (int i = 0; i < response.length(); i++) {
                            String username = response.getString(i);
                            profileUsernames.add(username);
                        }
                        profileAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("SearchActivity", "JSON profile parsing error", e);
                    }
                },
                error -> {
                    String errorMessage = (error.networkResponse != null) ?
                            "Status Code: " + error.networkResponse.statusCode :
                            "No response/UnknownHost.";
                    Log.e("SearchActivity", "Volley profile search error: " + error.toString());
                    Log.e("SearchActivity", "Volley Error Details: " + errorMessage);
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void searchSongs(String query) {
        String url = BASE_URL + "/search/songs/" + query;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        songList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject songJson = response.getJSONObject(i);
                            int songId = songJson.getInt("songId");
                            String songName = songJson.getString("songName");
                            String artistName = songJson.getString("artist");
                            songList.add(new Song(songId, songName, artistName));
                        }
                        songAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("SearchActivity", "JSON song parsing error", e);
                    }
                },
                error -> {
                    String errorMessage = (error.networkResponse != null) ?
                            "Status Code: " + error.networkResponse.statusCode :
                            "No response/UnknownHost.";
                    Log.e("SearchActivity", "Volley song search error: " + error.toString());
                    Log.e("SearchActivity", "Volley Error Details: " + errorMessage);
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void incrementProfileViews(String username) {
        String url = BASE_URL + "/search/profiles/" + username;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                response -> Log.d("SearchActivity", "Profile view incremented: " + username),
                error -> {
                    Log.e("SearchActivity", "Failed to increment views: " + error.toString());
                }
        );
        requestQueue.add(putRequest);
    }

    private void incrementSongSearches(int songId) {
        String url = BASE_URL + "/search/songs/" + songId;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                response -> Log.d("SearchActivity", "Song search incremented: " + songId),
                error -> {
                    Log.e("SearchActivity", "Failed to increment searches: " + error.toString());
                }
        );
        requestQueue.add(putRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}