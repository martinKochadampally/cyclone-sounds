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

/**
 * Activity for searching for different types of content within the app, such as user profiles and songs.
 * It allows users to select a search category and enter a query.
 */
public class SearchActivity extends AppCompatActivity {

    // Base URL for the backend API.
    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    // UI elements for search functionality.
    private SearchView searchView;
    private Spinner searchTypeSpinner;
    private Button searchSubmitButton;
    private ListView profileResultsList;
    private ListView songResultsList;
    // private ListView playlistResultsList;
    private TextView profilesHeader;
    private TextView songsHeader;
    // private TextView playlistsHeader;

    // Adapters for the result lists.
    private ArrayAdapter<String> profileAdapter;
    private ArrayAdapter<Song> songAdapter;
    // private ArrayAdapter<Playlist> playlistAdapter;

    // Data lists to hold search results.
    private ArrayList<String> profileUsernames;
    private ArrayList<Song> songList;
    // private ArrayList<Playlist> playlistList;

    // Volley request queue for handling network requests.
    private RequestQueue requestQueue;
    private String loggedInUsername;
    private String currentSearchType;

    /**
     * Inner class to represent a Song object.
     */
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

    /*
    private static class Playlist {
        private final String playlistName;
        private final String username;

        public Playlist(String playlistName, String username) {
            this.playlistName = playlistName;
            this.username = username;
        }

        public String getPlaylistName() {
            return playlistName;
        }

        @NonNull
        @Override
        public String toString() {
            return playlistName + " by " + username;
        }
    }
    */

    /**
     * Called when the activity is first created. Initializes UI components, adapters,
     * and sets up listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set up the toolbar with a title and back button.
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search");
        }

        loggedInUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");
        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI components.
        searchView = findViewById(R.id.search_view);
        searchTypeSpinner = findViewById(R.id.search_type_spinner);
        searchSubmitButton = findViewById(R.id.search_submit_button);
        profileResultsList = findViewById(R.id.profile_results_list);
        songResultsList = findViewById(R.id.song_results_list);
        // playlistResultsList = findViewById(R.id.playlist_results_list);
        profilesHeader = findViewById(R.id.profiles_header);
        songsHeader = findViewById(R.id.songs_header);
        // playlistsHeader = findViewById(R.id.playlists_header);

        // Initialize data lists and adapters.
        profileUsernames = new ArrayList<>();
        profileAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profileUsernames);
        profileResultsList.setAdapter(profileAdapter);

        songList = new ArrayList<>();
        songAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songList);
        songResultsList.setAdapter(songAdapter);

        // playlistList = new ArrayList<>();
        // playlistAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playlistList);
        // playlistResultsList.setAdapter(playlistAdapter);

        // Set up the various UI interaction components.
        setupSpinner();
        setupSearchView();
        setupButtonListener();
        setupClickListeners();
    }

    /**
     * Sets up the spinner for selecting the search type (e.g., "Profiles", "Songs").
     */
    private void setupSpinner() {
        // String[] searchTypes = {"Profiles", "Songs", "Playlists"};
        String[] searchTypes = {"Profiles", "Songs"}; // Removed "Playlists"
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, searchTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(spinnerAdapter);

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSearchType = parent.getItemAtPosition(position).toString();
                updateVisibleList();
                searchView.setQuery("", false); // Clear query on type change.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        currentSearchType = searchTypes[0]; // Default search type.
        updateVisibleList();
    }

    /**
     * Updates the visibility of the results lists and headers based on the selected search type.
     */
    private void updateVisibleList() {
        clearResults();

        // Hide all lists and headers initially.
        profilesHeader.setVisibility(View.GONE);
        profileResultsList.setVisibility(View.GONE);
        songsHeader.setVisibility(View.GONE);
        songResultsList.setVisibility(View.GONE);
        // playlistsHeader.setVisibility(View.GONE);
        // playlistResultsList.setVisibility(View.GONE);

        // Show the relevant list and header based on the current search type.
        if (currentSearchType.equals("Profiles")) {
            profilesHeader.setVisibility(View.VISIBLE);
            profileResultsList.setVisibility(View.VISIBLE);
            searchView.setQueryHint("Search for users...");
        } else if (currentSearchType.equals("Songs")) {
            songsHeader.setVisibility(View.VISIBLE);
            songResultsList.setVisibility(View.VISIBLE);
            searchView.setQueryHint("Search for songs or artists...");
        }
        /*
        else if (currentSearchType.equals("Playlists")) {
            playlistsHeader.setVisibility(View.VISIBLE);
            playlistResultsList.setVisibility(View.VISIBLE);
            searchView.setQueryHint("Search for playlists...");
        }
        */
    }

    /**
     * Sets up the SearchView, defining its behavior for text submission and changes.
     */
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus(); // Hide keyboard on submit.
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // No action on text change, we use a submit button.
            }
        });
    }

    /**
     * Sets up the listener for the search submit button.
     */
    private void setupButtonListener() {
        searchSubmitButton.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            if (query != null && !query.isEmpty()) {
                runSearch(query);
            }
            searchView.clearFocus(); // Hide keyboard after search.
        });
    }

    /**
     * Sets up click listeners for the items in the results lists.
     */
    private void setupClickListeners() {
        // Listener for profile search results.
        profileResultsList.setOnItemClickListener((parent, view, position, id) -> {
            String profileToView = profileUsernames.get(position);
            incrementProfileViews(profileToView);

            // Navigate to the ProfileActivity of the selected user.
            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", loggedInUsername);
            intent.putExtra("PROFILE_TO_VIEW", profileToView);
            startActivity(intent);
        });

        // Listener for song search results.
        songResultsList.setOnItemClickListener((parent, view, position, id) -> {
            Song clickedSong = songList.get(position);
            incrementSongSearches(clickedSong.getSongId());
            Toast.makeText(this, "Clicked on: " + clickedSong.toString(), Toast.LENGTH_SHORT).show();
        });

        /*
        playlistResultsList.setOnItemClickListener((parent, view, position, id) -> {
            Playlist clickedPlaylist = playlistList.get(position);
            incrementPlaylistSearches(clickedPlaylist.getPlaylistName());
            Toast.makeText(this, "Clicked on: " + clickedPlaylist.toString(), Toast.LENGTH_SHORT).show();
        });
        */
    }

    /**
     * Dispatches the search query to the appropriate search method based on the selected search type.
     * @param query The search query string.
     */
    private void runSearch(String query) {
        if (currentSearchType.equals("Profiles")) {
            searchProfiles(query);
        } else if (currentSearchType.equals("Songs")) {
            searchSongs(query);
        }
        /*
        else if (currentSearchType.equals("Playlists")) {
            searchPlaylists(query);
        }
        */
    }

    /**
     * Clears all current search results from the lists and notifies the adapters.
     */
    private void clearResults() {
        profileUsernames.clear();
        songList.clear();
        // playlistList.clear();
        profileAdapter.notifyDataSetChanged();
        songAdapter.notifyDataSetChanged();
        // playlistAdapter.notifyDataSetChanged();
    }

    /**
     * Performs a network request to search for user profiles.
     * @param query The username or partial username to search for.
     */
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

    /**
     * Performs a network request to search for songs.
     * @param query The song title or artist to search for.
     */
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

    /*
    private void searchPlaylists(String query) {
        String url = BASE_URL + "/search/playlist/" + query;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        playlistList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject playlistJson = response.getJSONObject(i);
                            String playlistName = playlistJson.getString("playlistName");
                            String username = playlistJson.getString("username");
                            playlistList.add(new Playlist(playlistName, username));
                        }
                        playlistAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("SearchActivity", "JSON playlist parsing error", e);
                    }
                },
                error -> {
                    String errorMessage = (error.networkResponse != null) ?
                            "Status Code: " + error.networkResponse.statusCode :
                            "No response/UnknownHost.";
                    Log.e("SearchActivity", "Volley playlist search error: " + error.toString());
                    Log.e("SearchActivity", "Volley Error Details: " + errorMessage);
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
    */

    /**
     * Sends a PUT request to the server to increment the view count for a profile.
     * @param username The username of the profile that was viewed.
     */
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

    /**
     * Sends a PUT request to the server to increment the search count for a song.
     * @param songId The ID of the song that was clicked.
     */
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

    /*
    private void incrementPlaylistSearches(String playlistName) {
        String url = BASE_URL + "/search/playlist/" + playlistName;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                response -> Log.d("SearchActivity", "Playlist search incremented: " + playlistName),
                error -> {
                    Log.e("SearchActivity", "Failed to increment playlist searches: " + error.toString());
                }
        );
        requestQueue.add(putRequest);
    }
    */

    /**
     * Handles action bar item clicks. Specifically, handles the back button press.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to the previous activity.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}