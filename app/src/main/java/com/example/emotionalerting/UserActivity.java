package com.example.emotionalerting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class UserActivity extends AppCompatActivity {

    private Button barbara;
    private Button martin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        barbara = findViewById(R.id.barbara);
        martin = findViewById(R.id.martin);

        barbara.setOnClickListener(view -> {
            openMainActivity("Martin", "Barbara");
        });

        martin.setOnClickListener(view -> {
            openMainActivity("Barbara", "Martin");
        });

    }

    private void openMainActivity(String targetUser, String user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("targetUser", targetUser);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}