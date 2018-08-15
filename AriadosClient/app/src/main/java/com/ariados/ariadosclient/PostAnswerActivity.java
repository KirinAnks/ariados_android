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

public class PostAnswerActivity extends AppCompatActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_answer);

        bt_save = findViewById(R.id.bt_save);
        bt_cancel = findViewById(R.id.bt_cancel);
        input_text = findViewById(R.id.input_text);
        txt_title = findViewById(R.id.txt_title);

        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        POST_TITLE = getIntent().getStringExtra("POST_TITLE");

        txt_title.setText("RE: " + POST_TITLE);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_text.getText().toString().isEmpty()){
                    Toast.makeText(PostAnswerActivity.this, "Text cannot be empty!", Toast.LENGTH_LONG).show();
                } else{
                    request = new ApiRequest();
                    data = new HashMap<>();
                    data.put("parent_title", POST_TITLE);
                    data.put("text", input_text.getText().toString());
                    try {
                        params = Utiles.getPostDataString(data);
                        request.execute("/posts/answer/", "POST", params, SESSION_KEY).get();
                        if (!request.getSuccess()) {
                            throw new RuntimeException("Error saving post");
                        }

                        Intent intent_post = new Intent(PostAnswerActivity.this, PostActivity.class);
                        intent_post.putExtra("SESSION_KEY", SESSION_KEY);
                        intent_post.putExtra("POST_TITLE", POST_TITLE);
                        PostAnswerActivity.this.startActivity(intent_post);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PostAnswerActivity.this, "Couldn't save post:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_post = new Intent(PostAnswerActivity.this, PostActivity.class);
                intent_post.putExtra("SESSION_KEY", SESSION_KEY);
                intent_post.putExtra("POST_TITLE", POST_TITLE);
                PostAnswerActivity.this.startActivity(intent_post);
            }
        });
    }


}
