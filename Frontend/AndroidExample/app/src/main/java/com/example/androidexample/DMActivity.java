package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dm);

        String currentUsername = getIntent().getStringExtra("CURRENT_USERNAME");
        String friendUsername = getIntent().getStringExtra("FRIEND_USERNAME");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("DM with " + friendUsername);
        }
    }
}