package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity represents a single "Jam" session. It includes a real-time chat, a feature
 * for suggesting songs, and admin controls for managing the jam's associated playlist.
 */
public class IndividualJamActivity extends AppCompatActivity {

    // API and WebSocket URLs
    private static final String HTTP_BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";
    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/api/playlists/";
    private static final String SONGS_SEARCH_URL = "http://coms-3090-008.class.las.iastate.edu:8080/search/songs";
    private String WEB_SOCKET_URL;

    // Volley and user/jam data
    private RequestQueue requestQueue;
    private String currentUsername;
    private String jamName;
    private String jamAdmin;

    // UI elements
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton, suggestSongButton, jamSettingsButton;

    // State flag
    private boolean jamHasPlaylist = false;

    // Chat components
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    // WebSocket client
    private WebSocketClient webSocketClient;

    /**
     * Called when the activity is first created. Initializes the UI, sets up the WebSocket connection,
     * fetches chat history, and configures user/admin specific UI elements.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individualjam);

        Toolbar toolbar = findViewById(R.id.dm_toolbar);
        setSupportActionBar(toolbar);

        currentUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");
        jamName = getIntent().getStringExtra("JAM_NAME");
        jamAdmin = getIntent().getStringExtra("JAM_ADMIN");

        Log.d("IndividualJamActivity", "Jam Admin: " + jamAdmin);

        // Construct WebSocket URL for this specific jam and user.
        WEB_SOCKET_URL = "ws://coms-3090-008.class.las.iastate.edu:8080/websocket/jams/" + jamName + "/"+ currentUsername;

        requestQueue = Volley.newRequestQueue(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(jamName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_btn);
        suggestSongButton = findViewById(R.id.suggest_song_btn);
        jamSettingsButton = findViewById(R.id.jam_settings_btn);

        suggestSongButton.setEnabled(false);

        // Show admin-specific buttons if the user is the jam admin.
        if (currentUsername != null && currentUsername.equals(jamAdmin)) {
            jamSettingsButton.setVisibility(View.VISIBLE);
        }

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, currentUsername);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(view -> sendMessage());
        suggestSongButton.setOnClickListener(view -> showSuggestSongDialog());
        jamSettingsButton.setOnClickListener(view -> showAdminMenu());

        if (jamName != null) {
            fetchChatHistory(jamName);
        }

        createWebSocketClient();
        fetchPlaylistsAndSetupSuggestionsButton();
    }

    /**
     * Handles the up navigation button press, finishing the activity.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Fetches the chat history for this jam from the server.
     * @param jamName The name of the jam.
     */
    private void fetchChatHistory(String jamName) {
        String url = HTTP_BASE_URL + "/api/jam/chatHistory/" + jamName;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        messageList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject messageJson = response.getJSONObject(i);
                            String content = messageJson.getString("content");
                            String[] messages = content.split("\\r?\\n"); // Split history which might be a single block.
                            for (String msg : messages) {
                                // Parse each line for sender and content, similar to WebSocket onMessage
                                String[] parts = msg.split(": ", 2);
                                if (parts.length == 2) {
                                    messageList.add(new ChatMessage(parts[0], parts[1]));
                                } else if (!msg.trim().isEmpty()){
                                    // Fallback for messages without a specific sender (e.g., system messages).
                                    messageList.add(new ChatMessage("System", msg));
                                }
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);

                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "JSON parsing error", e);
                    }
                },
                error -> {
                    // Log and display error if chat history fails to load.
                    String errorMessage = "Error loading chat history";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            errorMessage = "Error: " + error.networkResponse.statusCode + " " + responseBody;
                        } catch (Exception e) {
                            Log.e("IndividualJamActivity", "Error parsing error response", e);
                        }
                    }
                    Log.e("IndividualJamActivity", "Volley error: " + errorMessage, error);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Checks if a playlist for this jam exists and enables the "Suggest Song" button accordingly.
     */
    private void fetchPlaylistsAndSetupSuggestionsButton() {
        String url = URL_STRING_REQ + "owner/" + jamAdmin;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    // Check if the response contains a playlist with the same name as the jam.
                    if (response.toString().contains(jamName)) {
                        jamHasPlaylist = true;
                        suggestSongButton.setEnabled(true);
                    } else {
                        jamHasPlaylist = false;
                        suggestSongButton.setEnabled(false);
                    }
                },
                error -> {
                    jamHasPlaylist = false;
                    suggestSongButton.setEnabled(false);
                    Log.e("IndividualJamActivity", "Could not fetch jam playlists", error);
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Creates and connects the WebSocket client for real-time communication.
     */
    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI(WEB_SOCKET_URL);
        } catch (URISyntaxException e) {
            Log.e("IndividualJamActivity", "Invalid WebSocket URL", e);
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("IndividualJamActivity", "WebSocket Connected");
            }

            @Override
            public void onMessage(String message) {
                Log.d("IndividualJamActivity", "Received message: " + message);

                // Check if the message is a JSON object (for special messages like song suggestions).
                if (message.trim().startsWith("{")) {
                    try {
                        JSONObject messageJson = new JSONObject(message);
                        String type = messageJson.optString("type", "chat");

                        // Handle song suggestion messages.
                        if ("song_suggestion".equals(type)) {
                            if (currentUsername.equals(jamAdmin)) {
                                String song = messageJson.getString("song");
                                String artist = messageJson.getString("artist");
                                String suggester = messageJson.getString("suggester");
                                runOnUiThread(() -> showApprovalDialog(song, artist, suggester));
                            }
                            return;
                        }
                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "Error parsing JSON message", e);
                    }
                }

                // Handle plain text chat messages.
                runOnUiThread(() -> {
                    String[] parts = message.split(": ", 2);
                    if (parts.length == 2) {
                        messageList.add(new ChatMessage(parts[0], parts[1]));
                    } else {
                        messageList.add(new ChatMessage("System", message));
                    }
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("WebSocket", "Closed");
            }

            @Override
            public void onError(Exception ex) {
                Log.e("WebSocket", "Error", ex);
            }
        };
        webSocketClient.connect();
    }

    /**
     * Gets text from the input field and sends it as a chat message.
     */
    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }
        messageInput.setText("");
        sendMessageToBackend(new ChatMessage(currentUsername, content), jamName);
    }

    /**
     * Sends a chat message object to the backend via WebSocket.
     * @param message The ChatMessage to send.
     * @param jamName The name of the jam (context).
     */
    private void sendMessageToBackend(ChatMessage message, String jamName) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            // Send plain text content for chat messages.
            webSocketClient.send(message.getContent());
            Log.d(message.getSender(), message.getContent());
        } else {
            Toast.makeText(this, "Not connected to WebSocket", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows a dialog for users to search for and suggest a song.
     */
    private void showSuggestSongDialog() {
        if (!jamHasPlaylist) {
            Toast.makeText(this, "Admin has not created a playlist for this jam yet.", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suggest a Song");

        // Create a custom layout for the dialog.
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        LinearLayout searchLayout = new LinearLayout(this);
        searchLayout.setOrientation(LinearLayout.HORIZONTAL);

        final EditText searchInput = new EditText(this);
        searchInput.setHint("Enter song name");
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        searchInput.setLayoutParams(editTextParams);
        searchLayout.addView(searchInput);

        Button searchButton = new Button(this);
        searchButton.setText("Search");
        searchLayout.addView(searchButton);

        layout.addView(searchLayout);

        ScrollView scrollView = new ScrollView(this);
        TableLayout resultsTable = new TableLayout(this);
        resultsTable.setStretchAllColumns(true);

        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextViewForDialog("Song"));
        headerRow.addView(createTextViewForDialog("Artist"));
        headerRow.addView(createTextViewForDialog("")); // Placeholder for button column
        resultsTable.addView(headerRow);

        scrollView.addView(resultsTable);
        layout.addView(scrollView);

        AlertDialog dialog = builder.setView(layout)
                .setNegativeButton("Close", (d, which) -> d.cancel())
                .create();

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                searchSongsForSuggestion(query, resultsTable);
            } else {
                Toast.makeText(this, "Please enter a song to search", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    /**
     * Searches for songs to be suggested and populates the results in the dialog's table.
     * @param query The search query.
     * @param resultsTable The table to populate with search results.
     */
    private void searchSongsForSuggestion(String query, TableLayout resultsTable) {
        String url = SONGS_SEARCH_URL + "/" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        populateSuggestionSearchTable(response, resultsTable);
                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "JSON parsing error in search", e);
                        Toast.makeText(this, "Error parsing search results", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", "Error searching songs: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Song not found", Toast.LENGTH_SHORT).show();
                    int childCount = resultsTable.getChildCount();
                    if (childCount > 1) { // Keep header
                        resultsTable.removeViews(1, childCount - 1);
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Populates the search table within the suggestion dialog.
     * @param songs The JSONArray of songs found.
     * @param table The table to populate.
     * @throws JSONException If JSON parsing fails.
     */
    private void populateSuggestionSearchTable(JSONArray songs, TableLayout table) throws JSONException {
        int childCount = table.getChildCount();
        if (childCount > 1) { // Clear previous results, keep header
            table.removeViews(1, childCount - 1);
        }

        for (int i = 0; i < songs.length(); i++) {
            JSONObject song = songs.getJSONObject(i);
            String songName = song.optString("songName", "N/A");
            String artist = song.optString("artist", "N/A");

            TableRow tableRow = new TableRow(this);
            tableRow.addView(createTextViewForDialog(songName));
            tableRow.addView(createTextViewForDialog(artist));
            tableRow.addView(createSuggestButton(songName, artist));

            table.addView(tableRow);
        }
    }

    /**
     * Helper to create a TextView for the dialog table.
     */
    private TextView createTextViewForDialog(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setWidth(10);
        return textView;
    }

    /**
     * Creates a "Suggest" button for a song in the search results.
     * @param songName The name of the song.
     * @param artist The artist of the song.
     * @return The created Button.
     */
    private Button createSuggestButton(final String songName, final String artist) {
        Button button = new Button(this);
        button.setText("Suggest");
        button.setOnClickListener(v -> sendSongSuggestion(songName, artist));
        return button;
    }

    /**
     * Sends a song suggestion to the jam admin via WebSocket.
     * @param songName The name of the suggested song.
     * @param artist The artist of the suggested song.
     */
    private void sendSongSuggestion(String songName, String artist) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            JSONObject suggestionJson = new JSONObject();
            try {
                suggestionJson.put("type", "song_suggestion");
                suggestionJson.put("song", songName);
                suggestionJson.put("artist", artist);
                suggestionJson.put("suggester", currentUsername);
                suggestionJson.put("receiver", jamAdmin);
                webSocketClient.send(suggestionJson.toString());
                Log.d("Song Suggestion", suggestionJson.toString());
                Toast.makeText(this, "Song suggested!", Toast.LENGTH_SHORT).show();
                // Also send a plain text message to the chat about the suggestion.
                if (webSocketClient != null && webSocketClient.isOpen()) {
                    webSocketClient.send(currentUsername + " suggested adding '" + songName + "'");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Not connected to WebSocket", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays the admin menu with options for the jam.
     */
    private void showAdminMenu() {
        final CharSequence[] options = {"Create Jam Playlist", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Jam Settings");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Create Jam Playlist")) {
                createPlaylistRequest(currentUsername, jamName);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Sends a request to the server to create a new playlist for the jam.
     * @param user The username of the admin creating the playlist.
     * @param playlistName The name of the playlist, which is the same as the jam name.
     */
    private void createPlaylistRequest(final String user, final String playlistName) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ + "create",
                response -> {
                    Log.d("Volley Response", response.toString());
                    Toast.makeText(this, "Playlist Created", Toast.LENGTH_SHORT).show();
                    if (webSocketClient != null && webSocketClient.isOpen()) {
                        webSocketClient.send("Admin " + jamAdmin + " created a playlist for this jam");
                    }
                    fetchPlaylistsAndSetupSuggestionsButton(); // Re-check to enable suggestion button.
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Submission Error", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("playlistName", playlistName);
                params.put("username", user);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Shows a dialog to the jam admin to approve or reject a song suggestion.
     * @param song The name of the song.
     * @param artist The artist of the song.
     * @param suggester The user who suggested the song.
     */
    private void showApprovalDialog(String song, String artist, String suggester) {
        new AlertDialog.Builder(this)
                .setTitle("Song Suggestion")
                .setMessage(suggester + " suggested adding \"" + song + "\" to the playlist. Approve?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    addSongToPlaylist(song, artist);
                    Toast.makeText(this, song + " approved!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Reject", (dialog, which) -> {
                    if (webSocketClient != null && webSocketClient.isOpen()) {
                        webSocketClient.send("Suggestion for '" + song + "' was denied by the admin");
                    }
                })
                .show();
    }

    /**
     * Adds an approved song to the jam's playlist.
     * @param songName The name of the song.
     * @param artist The artist of the song.
     */
    private void addSongToPlaylist(final String songName, final String artist) {
        String url = URL_STRING_REQ + currentUsername + "/" + jamName + "/add";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Volley Response", "Add song: " + response);
                    Toast.makeText(getApplicationContext(), "Song added", Toast.LENGTH_SHORT).show();
                    if (webSocketClient != null && webSocketClient.isOpen()) {
                        webSocketClient.send("Suggestion for '" + songName + "' was approved by the admin");
                    }
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
     * Called when the activity is being destroyed. Ensures the WebSocket client is closed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}