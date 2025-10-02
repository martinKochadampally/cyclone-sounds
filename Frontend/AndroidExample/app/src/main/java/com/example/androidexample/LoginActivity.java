package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        signupButton = findViewById(R.id.login_signup_btn);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://d32495e1-408f-47f7-a807-a1477958cb7f.mock.pstmn.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        loginButton.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Attempting to log in...", Toast.LENGTH_SHORT).show();

            Call<LoginResponse> call = apiService.loginUser();

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        Log.d("API_LOGIN_SUCCESS", "Status: " + loginResponse.getStatus());
                        Log.d("API_LOGIN_SUCCESS", "Token: " + loginResponse.getToken());

                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                    } else {
                        Log.e("API_ERROR", "Login response error: " + response.code());
                        Toast.makeText(getApplicationContext(), "Login failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("API_FAILURE", "Network request failed: " + t.getMessage());
                    Toast.makeText(getApplicationContext(), "Network error. Please try again.", Toast.LENGTH_LONG).show();
                }
            });
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}