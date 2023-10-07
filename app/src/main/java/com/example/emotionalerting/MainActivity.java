package com.example.emotionalerting;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    private ImageView happy;
    private ImageView sad;
    private Button update;
    private TextView emotion;

    private String baseUrl = "http://10.0.2.2:8080/message";
    private String user = "Barbara";
    private String targetUser = "Martin";

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        targetUser = intent.getStringExtra("targetUser");


        url = baseUrl + "?name=" + targetUser;

        RequestQueue queue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        happy = findViewById(R.id.imageView);
        sad = findViewById(R.id.imageView2);

        update = findViewById(R.id.button2);
        emotion = findViewById(R.id.textView);

        JsonObjectRequest request = createUpdateEmotionRequest();
        createUpdateListener(queue, request);

        happy.setOnClickListener(view -> {
            try {
                queue.add(createPostEmotionRequest(user, "HAPPY"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sad.setOnClickListener(view -> {
            try {
                queue.add(createPostEmotionRequest(user, "SAD"));
            } catch (Exception e) {
                e.printStackTrace();
            }
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