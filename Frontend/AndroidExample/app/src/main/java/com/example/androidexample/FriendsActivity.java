package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activity for managing a user's friends. It displays the friend list,
 * allows sending new friend requests, and handling pending requests.
 */
public class FriendsActivity extends AppCompatActivity {

    // Base URL for the backend API.
    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    // UI elements.
    private ListView friendsListView;
    private TextView emptyListText;
    private Button addFriendButton;
    private Button pendingRequestsButton;
    private Button homeButton, musicButton, createButton, jamsButton, profileButton;

    // Adapter and data source for the friends list.
    private ArrayAdapter<String> friendsAdapter;
    private ArrayList<String> friendsList;
    private String currentUsername;
    private RequestQueue requestQueue;

    /**
     * Called when the activity is first created. Initializes UI components, sets up listeners,
     * and fetches the user's friend list.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        currentUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");

        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI components.
        friendsListView = findViewById(R.id.friends_list_view);
        emptyListText = findViewById(R.id.empty_list_text);
        addFriendButton = findViewById(R.id.add_friend_button);
        pendingRequestsButton = findViewById(R.id.pending_requests_button);
        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        // Initialize the friends list and its adapter.
        friendsList = new ArrayList<>();
        friendsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(friendsAdapter);

        // Set the view to display when the friends list is empty.
        friendsListView.setEmptyView(emptyListText);

        // Set up button listeners.
        addFriendButton.setOnClickListener(view -> showAddFriendDialog());
        pendingRequestsButton.setOnClickListener(view -> fetchPendingRequests());

        // Set up listener for clicks on friends in the list.
        friendsListView.setOnItemClickListener((parent, view, position, id) -> {
            String friendUsername = friendsList.get(position);
            showFriendOptionsDialog(friendUsername);
        });

        if (currentUsername != null) {
            fetchFriendsFromDatabase(currentUsername);
        }

        setupNavigation();
    }

    /**
     * Displays a dialog with options for a selected friend (e.g., View Profile, Send DM).
     * @param friendUsername The username of the friend that was clicked.
     */
    private void showFriendOptionsDialog(String friendUsername) {
        CharSequence[] options = {"View Profile", "Send DM", "Play Favorite Song"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(friendUsername);
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("View Profile")) {
                Intent profileIntent = new Intent(FriendsActivity.this, ProfileActivity.class);
                profileIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
                profileIntent.putExtra("PROFILE_TO_VIEW", friendUsername);
                startActivity(profileIntent);
            } else if (options[item].equals("Send DM")) {
                Intent dmIntent = new Intent(FriendsActivity.this, DMActivity.class);
                dmIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
                dmIntent.putExtra("FRIEND_USERNAME", friendUsername);
                startActivity(dmIntent);
            } else if (options[item].equals("Play Favorite Song")) {
                MusicPlayer.getInstance().playFavoriteSong(this, friendUsername);
            }
        });
        builder.show();
    }

    /**
     * Displays a dialog for the user to enter the username of a friend to add.
     */
    private void showAddFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Friend");

        final EditText input = new EditText(this);
        input.setHint("Enter username");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String friendUsername = input.getText().toString().trim();
            if (!friendUsername.isEmpty()) {
                sendFriendRequestToDatabase(friendUsername);
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Fetches the list of accepted friends for the current user from the database.
     * @param username The username of the current user.
     */
    private void fetchFriendsFromDatabase(String username) {
        String url = BASE_URL + "/api/friends/accepted/" + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        friendsList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject friendProfile = response.getJSONObject(i);
                            String friendUsername = friendProfile.getString("username");
                            friendsList.add(friendUsername);
                        }
                        friendsAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("FriendsActivity", "JSON parsing error", e);
                    }
                },
                error -> {
                    Log.e("FriendsActivity", "Volley error", error);
                    Toast.makeText(this, "Error loading friends", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Sends a friend request to another user via a POST request to the server.
     * @param friendUsername The username of the user to send the request to.
     */
    private void sendFriendRequestToDatabase(String friendUsername) {
        String url = BASE_URL + "/api/friends/request";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("requester", currentUsername);
            requestBody.put("receiver", friendUsername);
        } catch (JSONException e) {
            Log.e("FriendsActivity", "JSON creation error", e);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Toast.makeText(this, "Friend request sent!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    String errorMessage = "Failed to send request";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            errorMessage = "Error: " + error.networkResponse.statusCode + " " + responseBody;
                        } catch (Exception e) {
                            Log.e("FriendsActivity", "Error parsing error response", e);
                        }
                    }
                    Log.e("FriendsActivity", "Volley error: " + errorMessage, error);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Fetches all pending incoming friend requests for the current user.
     */
    private void fetchPendingRequests() {
        String url = BASE_URL + "/api/friends/pending/" + currentUsername;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        ArrayList<String> displayNames = new ArrayList<>();
                        ArrayList<String> requestIds = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject friendRequest = response.getJSONObject(i);
                            String requestId = friendRequest.getString("id");
                            JSONObject requesterProfile = friendRequest.getJSONObject("requester");
                            String requesterUsername = requesterProfile.getString("username");

                            displayNames.add(requesterUsername);
                            requestIds.add(requestId);
                        }
                        showPendingRequestsDialog(displayNames, requestIds);
                    } catch (JSONException e) {
                        Log.e("FriendsActivity", "JSON parsing error", e);
                    }
                },
                error -> {
                    Log.e("FriendsActivity", "Volley error", error);
                    Toast.makeText(this, "Error fetching requests", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Displays a dialog listing all pending friend requests.
     * @param displayNames List of usernames of users who sent requests.
     * @param requestIds List of corresponding request IDs.
     */
    private void showPendingRequestsDialog(ArrayList<String> displayNames, ArrayList<String> requestIds) {
        if (displayNames.isEmpty()) {
            Toast.makeText(this, "No pending requests", Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence[] items = displayNames.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pending Requests");
        builder.setItems(items, (dialog, which) -> {
            String selectedUsername = displayNames.get(which);
            String selectedRequestId = requestIds.get(which);
            showRespondToRequestDialog(selectedUsername, selectedRequestId);
        });
        builder.show();
    }

    /**
     * Shows a dialog to accept or decline a specific friend request.
     * @param username The username of the requester.
     * @param requestId The ID of the friend request.
     */
    private void showRespondToRequestDialog(String username, String requestId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Respond to " + username);
        builder.setMessage("Do you want to accept this friend request?");

        builder.setPositiveButton("Accept", (dialog, which) -> sendFriendResponse(requestId, "ACCEPTED"));
        builder.setNegativeButton("Decline", (dialog, which) -> sendFriendResponse(requestId, "DECLINED"));
        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /**
     * Sends a response (accept or decline) for a friend request to the server.
     * @param requestId The ID of the friend request being responded to.
     * @param status The response status ("ACCEPTED" or "DECLINED").
     */
    private void sendFriendResponse(String requestId, String status) {
        String url = BASE_URL + "/api/friends/respond/" + requestId;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("status", status);
        } catch (JSONException e) {
            Log.e("FriendsActivity", "JSON creation error", e);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    if ("ACCEPTED".equals(status)) {
                        Toast.makeText(this, "Friend added!", Toast.LENGTH_SHORT).show();
                        fetchFriendsFromDatabase(currentUsername); // Refresh the friends list.
                    } else {
                        Toast.makeText(this, "Request declined", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("FriendsActivity", "Volley error on response", error);
                    Toast.makeText(this, "Error responding to request", Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Sets up the navigation buttons at the bottom of the screen.
     */
    private void setupNavigation() {
        homeButton.setOnClickListener(view -> navigateTo(HomeActivity.class));
        musicButton.setOnClickListener(view -> navigateTo(MusicActivity.class));
        createButton.setOnClickListener(view -> navigateTo(CreateActivity.class));
        jamsButton.setOnClickListener(view -> navigateTo(JamsActivity.class));

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(FriendsActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            intent.putExtra("PROFILE_TO_VIEW", currentUsername); // Navigate to own profile.
            startActivity(intent);
        });
    }

    /**
     * Helper method to navigate to another activity, passing the current username.
     * @param activityClass The class of the activity to navigate to.
     */
    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(FriendsActivity.this, activityClass);
        intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayer.getInstance().release();
    }
}
