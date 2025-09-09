package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Import Handler
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CounterActivity extends AppCompatActivity {

    private TextView numberTxt;
    private Button increaseBtn;
    private Button decreaseBtn;
    private Button backBtn;
    private Button startAutoBtn;
    private Button stopAutoBtn;

    private int counter = 0;

    // 3. Declare a Handler to manage the timed events
    private Handler autoIncrementHandler = new Handler();
    private boolean isAutoIncrementing = false;


    private Runnable autoIncrementRunnable = new Runnable() {
        @Override
        public void run() {
            counter++; // Increment the counter
            numberTxt.setText(String.valueOf(counter));
            autoIncrementHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        // Initialize all UI elements
        numberTxt = findViewById(R.id.number);
        increaseBtn = findViewById(R.id.counter_increase_btn);
        decreaseBtn = findViewById(R.id.counter_decrease_btn);
        backBtn = findViewById(R.id.counter_back_btn);
        startAutoBtn = findViewById(R.id.start_auto_btn);
        stopAutoBtn = findViewById(R.id.stop_auto_btn);

        // Manual increase button
        increaseBtn.setOnClickListener(v -> {
            numberTxt.setText(String.valueOf(++counter));
        });

        // Manual decrease button
        decreaseBtn.setOnClickListener(v -> {
            numberTxt.setText(String.valueOf(--counter));
        });

        // Back button
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CounterActivity.this, MainActivity.class);
            intent.putExtra("NUM", String.valueOf(counter));
            startActivity(intent);
        });


        startAutoBtn.setOnClickListener(v -> {
            if (!isAutoIncrementing) {
                isAutoIncrementing = true;
                autoIncrementHandler.post(autoIncrementRunnable);
            }
        });


        stopAutoBtn.setOnClickListener(v -> {
            if (isAutoIncrementing) {
                isAutoIncrementing = false;
                autoIncrementHandler.removeCallbacks(autoIncrementRunnable);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        isAutoIncrementing = false;
        autoIncrementHandler.removeCallbacks(autoIncrementRunnable);
    }
}