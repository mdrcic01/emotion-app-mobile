package com.example.emotionalerting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button update;
    private TextView emotion;

    private String baseUrl = "https://martin-ubuntu-server.tail56ffbc.ts.net:8443/message";
    private String user = "Barbara";
    private String targetUser = "Martin";

    String url;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        targetUser = intent.getStringExtra("targetUser");

        url = baseUrl + "?name=" + targetUser;

        RequestQueue queue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, ImageView> emojis = new HashMap<>();

        emojis.put("HAPPY", (ImageView) findViewById(R.id.happyButton));
        emojis.put("HORNY", (ImageView) findViewById(R.id.hornyButton));
        emojis.put("SAD", (ImageView) findViewById(R.id.sadButton));
        emojis.put("ANGRY", (ImageView) findViewById(R.id.angryButton));
        emojis.put("SLEEPY", (ImageView) findViewById(R.id.sleepyButton));
        emojis.put("SICK", (ImageView) findViewById(R.id.sickButton));
        emojis.put("ANNOYED", (ImageView) findViewById(R.id.annoyedButton));

        update = findViewById(R.id.updateButton);
        emotion = findViewById(R.id.emotionMessage);

        JsonObjectRequest request = createUpdateEmotionRequest();
        createUpdateListener(queue, request);

        emojis.forEach((emotionString, emotionView) -> {
            emotionView.setOnClickListener(view -> {
                try {
                    queue.add(createPostEmotionRequest(user, emotionString));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void createUpdateListener(RequestQueue queue, JsonObjectRequest request) {
        update.setOnClickListener(view -> {
            System.out.println(url);
            queue.add(request);
        });
    }

    private JsonObjectRequest createUpdateEmotionRequest() {
        return new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                emotion.setText(String.format("%s is %s!\nUpdated at %s", targetUser, response.getString("emotion"), parseDate(response.getString("lastUpdatedTimestamp"))));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> System.out.println("something happened"));
    }

    private String parseDate(String dateTime) {
        String[] split = dateTime.split("T");
        String[] date = split[0].split("-");

        return String.format("%s.%s.%s %s", date[2], date[1], date[0], split[1]);
    }

    private StringRequest createPostEmotionRequest(String user, String emotion) {
        return new StringRequest(Request.Method.POST, baseUrl,
                response -> Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG)) {
            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", user);
                    jsonObject.put("emotion", emotion);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
    }

}