package com.ariados.ariadosclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONObject;

import java.util.HashMap;

public class PostEditActivity extends AppCompatActivity {
    Button bt_save;
    Button bt_cancel;
    TextView txt_title;
    EditText input_text;
    HashMap<String, String> data;
    ApiRequest request;
    String params;
    JSONObject response;

    String SESSION_KEY;
    String POST_TITLE;
    String POST_TEXT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        bt_save = findViewById(R.id.bt_save);
        bt_cancel = findViewById(R.id.bt_cancel);
        input_text = findViewById(R.id.input_text);
        txt_title = findViewById(R.id.txt_title);

        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        POST_TITLE = getIntent().getStringExtra("POST_TITLE");
        POST_TEXT = getIntent().getStringExtra("POST_TEXT");

        txt_title.setText(POST_TITLE);
        input_text.setText(POST_TEXT);

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request = new ApiRequest();
                data = new HashMap<>();
                data.put("title", POST_TITLE);
                data.put("text", input_text.getText().toString());
                try {
                    params = Utiles.getPostDataString(data);
                    request.execute("/posts/update/", "POST", params, SESSION_KEY).get();
                    if (!request.getSuccess()){
                        throw new RuntimeException("Error saving post");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PostEditActivity.this, "Couldn't save post:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_posts = new Intent(PostEditActivity.this, PostsActivity.class);
                intent_posts.putExtra("SESSION_KEY", SESSION_KEY);
                PostEditActivity.this.startActivity(intent_posts);
                Toast.makeText(PostEditActivity.this, "Succesfully saved!", Toast.LENGTH_LONG).show();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_posts = new Intent(PostEditActivity.this, PostsActivity.class);
                intent_posts.putExtra("SESSION_KEY", SESSION_KEY);
                PostEditActivity.this.startActivity(intent_posts);
            }
        });
    }


}
