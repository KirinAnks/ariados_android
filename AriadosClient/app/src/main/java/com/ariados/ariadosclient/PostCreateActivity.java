package com.ariados.ariadosclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONObject;

import java.util.HashMap;

public class PostCreateActivity extends AppCompatActivity {
    Button bt_save;
    Button bt_cancel;
    EditText input_title;
    EditText input_text;
    Spinner select_viewers;

    HashMap<String, String> data;
    ApiRequest request;
    String params;
    JSONObject response;
    private String[] viewers = new String[]{"GLOBAL", "INSTINCT", "MYSTIC", "VALOR"};

    String SESSION_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);

        bt_save = findViewById(R.id.bt_save);
        bt_cancel = findViewById(R.id.bt_cancel);
        input_text = findViewById(R.id.input_text);
        input_title = findViewById(R.id.input_title);
        select_viewers = findViewById(R.id.select_viewers);

        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");

        // Establecemos las opciones para el Spinner -dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PostCreateActivity.this,
                android.R.layout.simple_spinner_item, viewers);
        select_viewers.setAdapter(adapter);

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_title.getText().toString().isEmpty() || input_text.getText().toString().isEmpty()){
                    Toast.makeText(PostCreateActivity.this, "Title or text cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    request = new ApiRequest();
                    data = new HashMap<>();
                    data.put("title", input_title.getText().toString());
                    data.put("text", input_text.getText().toString());
                    data.put("viewers", select_viewers.getSelectedItem().toString());
                    try {
                        params = Utiles.getPostDataString(data);
                        request.execute("/posts/create/", "POST", params, SESSION_KEY).get();
                        if (!request.getSuccess()) {
                            throw new RuntimeException("Error creating post");
                        }

                        // Cambiar de "layout/activity" al pulsar este botón
                        Intent intent_posts = new Intent(PostCreateActivity.this, PostsActivity.class);
                        intent_posts.putExtra("SESSION_KEY", SESSION_KEY);
                        PostCreateActivity.this.startActivity(intent_posts);
                        Toast.makeText(PostCreateActivity.this, "Succesfully created!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PostCreateActivity.this, "Couldn't create post:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_posts = new Intent(PostCreateActivity.this, PostsActivity.class);
                intent_posts.putExtra("SESSION_KEY", SESSION_KEY);
                PostCreateActivity.this.startActivity(intent_posts);
            }
        });
    }


}
