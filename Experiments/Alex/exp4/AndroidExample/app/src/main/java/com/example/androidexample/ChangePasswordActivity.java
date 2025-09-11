package com.example.androidexample;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPass;
    private EditText newPass;
    private EditText confirmPass;
    private Button changeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPass = findViewById(R.id.old_password_edt);  // link to password edtext in the Change Password activity XML
        newPass = findViewById(R.id.new_password_edt);  // link to confirm edtext in the Change Password activity XML
        confirmPass = findViewById(R.id.confirm_password_edt); // link to confirm edtext in the Change Password activity XML
        changeButton = findViewById(R.id.change_btn);    // link to change password button in the Change Password activity XML
        Bundle extras = getIntent().getExtras();
        Toast.makeText(getApplicationContext(), "Changing Password", Toast.LENGTH_LONG).show();
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPass.getText().toString();
                String newPassword = newPass.getText().toString();
                String confirmPassword = confirmPass.getText().toString();
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                if (oldPassword.equals(extras.getString("PASSWORD"))) {

                    if (newPassword.equals(confirmPassword)) {
                        Toast.makeText(getApplicationContext(), "Changing password", Toast.LENGTH_LONG).show();
                        intent.putExtra("PASSWORD", newPassword);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
