package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/*

1. To run this project, open the directory "Android Example", otherwise it may not recognize the file structure properly

2. Ensure you are using a compatible version of gradle, to do so you need to check 2 files.

    AndroidExample/Gradle Scripts/build.gradle
    Here, you will have this block of code. Ensure it is set to a compatible version,
    in this case 8.12.2 should be sufficient:
        plugins {
            id 'com.android.application' version '8.12.2' apply false
        }

    Gradle Scripts/gradle-wrapper.properties

3. This file is what actually determines the Gradle version used, 8.13 should be sufficient.
    "distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-bin.zip" ---Edit the version if needed

4. You might be instructed by the plugin manager to upgrade plugins, accept it and you may execute the default selected options.

5. Press "Sync project with gradle files" located at the top right of Android Studio,
   once this is complete you will be able to run the app

   This version is compatible with both JDK 17 and 21. The Java version you want to use can be
   altered in Android Studio->Settings->Build, Execution, Deployment->Build Tools->Gradle

 */


public class HomeActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextView usernameText;  // define username textview variable
    private Button profileButton;    // define profile button variable
    private Button musicButton;      // define music button variable
    private Button jamsButton;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.home_msg_txt);      // link to message textview in the Main activity XML
        usernameText = findViewById(R.id.home_username_txt);// link to username textview in the Main activity XML
        musicButton = findViewById(R.id.music_button_btn);    // link to music button in the Main activity XML
        profileButton = findViewById(R.id.profile_button_btn);// link to profile button in the Main activity XML
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            messageText.setText("Cyclone Sounds");
            usernameText.setVisibility(View.INVISIBLE);             // set username text invisible initially
        } else {
            messageText.setText("Welcome");
            usernameText.setText(extras.getString("USERNAME")); // this will come from LoginActivity

        }

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MusicActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        jamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, JamsActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

    }
}