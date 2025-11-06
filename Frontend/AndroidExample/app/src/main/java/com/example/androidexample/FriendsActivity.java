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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    private ListView friendsListView;
    private TextView emptyListText;
    private Button addFriendButton;
    private Button pendingRequestsButton;

    private ArrayAdapter<String> friendsAdapter;
    private ArrayList<String> friendsList;
    private String currentUsername;
    private RequestQueue requestQueue;

    private Button homeButton, musicButton, createButton, jamsButton, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        currentUsername = getIntent().getStringExtra("USERNAME");

        requestQueue = Volley.newRequestQueue(this);

        friendsListView = findViewById(R.id.friends_list_view);
        emptyListText = findViewById(R.id.empty_list_text);
        addFriendButton = findViewById(R.id.add_friend_button);
        pendingRequestsButton = findViewById(R.id.pending_requests_button);

        homeButton = findViewById(R.id.home_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);

        friendsList = new ArrayList<>();
        friendsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(friendsAdapter);

        friendsListView.setEmptyView(emptyListText);

        addFriendButton.setOnClickListener(view -> showAddFriendDialog());

        pendingRequestsButton.setOnClickListener(view -> fetchPendingRequests());

        friendsListView.setOnItemClickListener((parent, view, position, id) -> {
            String friendUsername = friendsList.get(position);
            showFriendOptionsDialog(friendUsername);
        });

        if (currentUsername != null) {
            fetchFriendsFromDatabase(currentUsername);
        }

        setupNavigation();
    }

    private void showFriendOptionsDialog(String friendUsername) {
        CharSequence[] options = {"View Profile", "Send DM"};
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
                dmIntent.putExtra("CURRENT_USERNAME", currentUsername);
                dmIntent.putExtra("FRIEND_USERNAME", friendUsername);
                startActivity(dmIntent);
            }
        });
        builder.show();
    }

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
                    try {
                        String status = response.getString("status");
                        if ("PENDING".equals(status)) {
                            Toast.makeText(this, "Friend request sent!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("FriendsActivity", "JSON response error", e);
                    }
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

    private void showRespondToRequestDialog(String username, String requestId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Respond to " + username);
        builder.setMessage("Do you want to accept this friend request?");

        builder.setPositiveButton("Accept", (dialog, which) -> {
            sendFriendResponse(requestId, "ACCEPTED");
        });

        builder.setNegativeButton("Decline", (dialog, which) -> {
            sendFriendResponse(requestId, "DECLINED");
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

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
                        fetchFriendsFromDatabase(currentUsername);
                    } else {
                        Toast.makeText(this, "Request declined", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Error loading friends";
                    // Check if the server sent a response
                    if (error.networkResponse != null) {
                        // Get the HTTP status code (e.g., 404, 500)
                        int statusCode = error.networkResponse.statusCode;
                        errorMessage += " (Status " + statusCode + ")"; // e.g., "Error loading friends (Status 404)"

                        // Try to get the server's error message from the response body
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("FriendsActivity", "Volley Error " + statusCode + ": " + responseBody, error);
                        } catch (Exception e) {
                            Log.e("FriendsActivity", "Volley error parsing response", e);
                        }
                    } else {
                        // No network response (e.g., couldn't connect, DNS error)
                        Log.e("FriendsActivity", "Volley error (no network response)", error);
                    }

                    // Show the more detailed error on screen
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void setupNavigation() {
        homeButton.setOnClickListener(view -> navigateTo(HomeActivity.class));
        musicButton.setOnClickListener(view -> navigateTo(MusicActivity.class));
        createButton.setOnClickListener(view -> navigateTo(CreateActivity.class));
        jamsButton.setOnClickListener(view -> navigateTo(JamsActivity.class));

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(FriendsActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            intent.putExtra("PROFILE_TO_VIEW", currentUsername);
            startActivity(intent);
        });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(FriendsActivity.this, activityClass);
        intent.putExtra("USERNAME", currentUsername);
        startActivity(intent);
    }
}