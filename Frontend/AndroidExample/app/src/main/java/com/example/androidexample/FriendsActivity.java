package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private ListView friendsListView;
    private TextView emptyListText;
    private Button addFriendButton;
    private ArrayAdapter<String> friendsAdapter;
    private ArrayList<String> friendsList;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        currentUsername = getIntent().getStringExtra("USERNAME");

        friendsListView = findViewById(R.id.friends_list_view);
        emptyListText = findViewById(R.id.empty_list_text);
        addFriendButton = findViewById(R.id.add_friend_button);

        friendsList = new ArrayList<>();

        friendsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, friendsList);
        friendsListView.setAdapter(friendsAdapter);

        friendsListView.setEmptyView(emptyListText);

        addFriendButton.setOnClickListener(view -> showAddFriendDialog());

        friendsListView.setOnItemClickListener((parent, view, position, id) -> {
            String friendUsername = friendsList.get(position);
            showFriendOptionsDialog(friendUsername);
        });

    }

    private void showFriendOptionsDialog(String friendUsername) {
        CharSequence[] options = {"View Profile", "Send DM"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(friendUsername);
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("View Profile")) {
                Intent profileIntent = new Intent(FriendsActivity.this, ProfileActivity.class);
                profileIntent.putExtra("USERNAME", friendUsername);
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
                addFriendToDatabase(friendUsername);
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchFriendsFromDatabase(String username) {
        Toast.makeText(this, "Fetching friends... (implement this)", Toast.LENGTH_SHORT).show();
    }

    private void addFriendToDatabase(String friendUsername) {
        friendsList.add(friendUsername);
        friendsAdapter.notifyDataSetChanged();
        Toast.makeText(this, friendUsername + " added (locally)", Toast.LENGTH_SHORT).show();
    }
}