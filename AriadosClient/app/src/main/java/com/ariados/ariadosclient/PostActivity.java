package com.ariados.ariadosclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariados.ariadosclient.models.Answer;
import com.ariados.ariadosclient.models.Post;
import com.ariados.ariadosclient.models.Trainer;
import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    TextView txt_title;
    TextView txt_text;
    TextView txt_metadata;
    TextView txt_likes;
    TextView txt_dislikes;
    String SESSION_KEY;
    String POST_TITLE;
    ImageView img_like;
    ImageView img_dislike;
    Button bt_edit;
    Button bt_delete;
    ApiRequest request;
    ApiRequest request_author;
    ApiRequest request_votes;
    ApiRequest request_answers;
    JSONObject response;
    JSONObject response_author;
    JSONObject response_votes;
    Post post = new Post();
    HashMap<String, String> data;
    String params;
    JSONArray response_array;
    Boolean success;
    String message;
    List<Answer> answers;
    ArrayAdapter<String> arrayAdapter;
    ListView list_answers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        txt_title = findViewById(R.id.txt_title);
        txt_text = findViewById(R.id.txt_text);
        txt_metadata = findViewById(R.id.txt_metadata);
        txt_likes = findViewById(R.id.txt_likes);
        txt_dislikes = findViewById(R.id.txt_dislikes);
        txt_title = findViewById(R.id.txt_title);
        img_like = findViewById(R.id.img_like);
        img_dislike = findViewById(R.id.img_dislike);
        bt_edit = findViewById(R.id.bt_edit);
        bt_delete = findViewById(R.id.bt_delete);

        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        POST_TITLE = getIntent().getStringExtra("POST_TITLE");

        display_edit_delete_buttons();

        // Button listeners
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request = new ApiRequest();
                data = new HashMap<>();
                data.put("title", POST_TITLE);
                try {
                    params = Utiles.getPostDataString(data);
                    request.execute("/posts/delete/?" + params, "GET", "", SESSION_KEY).get();
                    response = request.getResponse();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PostActivity.this, "Couldn't delete post", Toast.LENGTH_LONG).show();
                }

                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_posts = new Intent(PostActivity.this, PostsActivity.class);
                intent_posts.putExtra("SESSION_KEY", SESSION_KEY);
                PostActivity.this.startActivity(intent_posts);
                Toast.makeText(PostActivity.this, "Succesfully deleted", Toast.LENGTH_LONG).show();
            }
        });

        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_edit = new Intent(PostActivity.this, PostEditActivity.class);
                intent_edit.putExtra("SESSION_KEY", SESSION_KEY);
                intent_edit.putExtra("POST_TITLE", POST_TITLE);
                intent_edit.putExtra("POST_TEXT", txt_text.getText().toString());
                PostActivity.this.startActivity(intent_edit);
            }
        });


        // Para que aparezcan los datos a mostrar hay que consultar a la api
        try {
            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request = new ApiRequest();
            data = new HashMap<>();
            data.put("title", POST_TITLE);
            params = Utiles.getPostDataString(data);
            request.execute("/posts/get/?" + params, "GET", "", SESSION_KEY).get();
            response = request.getResponse();

            if (request.getSuccess()) {
                post = new Post(response);

                txt_title.setText(post.getTitle());
                txt_text.setText(post.getText());
                txt_metadata.setText(String.format("posted by %s at %s", post.getCreator(), post.getLast_update()));

            } else {
                Toast.makeText(PostActivity.this, response.getString("error"), Toast.LENGTH_LONG).show();
//                finish();
            }

        } catch (Exception e) {
            Toast.makeText(PostActivity.this, "An error occured: " + e.toString(), Toast.LENGTH_LONG).show();
            request.cancel(true);
            e.printStackTrace();
//            finish();

        }
        //Consulta de los votos
        get_votes();

        list_answers = findViewById(R.id.list_answers);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        list_answers.setAdapter(arrayAdapter);

        response_array = new JSONArray();
        success = false;
        message = "";

        try {
            answers = new ArrayList<>();
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();

            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request_answers = new ApiRequest();
            request_answers.execute("/posts/answers/?" + params, "GET", "", SESSION_KEY).get();

            if (request_answers.getSuccess() && request_answers.getIsArray()) {
                response_array = request_answers.getResponseArray();
                ArrayList<JSONObject> json_list = Utiles.castToJSONList(response_array);
                for (JSONObject json : json_list) {
                    answers.add(new Answer(json));
                }

                List<String> strings = new ArrayList<>(answers.size());
                for (Answer t : answers) {
                    strings.add(t.toString());
                }

                arrayAdapter.addAll(strings);
                arrayAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            request_answers.cancel(true);
            e.printStackTrace();
        }

        // Button listeners
        img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_votes = new ApiRequest();
                data = new HashMap<>();
                data.put("title", POST_TITLE);
                data.put("type", "LIKE");
                try {
                    params = Utiles.getPostDataString(data);
                    request_votes.execute("/posts/vote/?" + params, "GET", "", SESSION_KEY).get();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PostActivity.this, "Already voted!", Toast.LENGTH_SHORT).show();
                    request_votes.cancel(true);
                }

                get_votes();
                Toast.makeText(PostActivity.this, "Upvoted!", Toast.LENGTH_SHORT).show();

            }
        });
        // Button listeners
        img_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_votes = new ApiRequest();
                data = new HashMap<>();
                data.put("title", POST_TITLE);
                data.put("type", "DISLIKE");
                try {
                    params = Utiles.getPostDataString(data);
                    request_votes.execute("/posts/vote/?" + params, "GET", "", SESSION_KEY).get();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PostActivity.this, "Already voted!", Toast.LENGTH_SHORT).show();
                    request_votes.cancel(true);
                }

                get_votes();
                Toast.makeText(PostActivity.this, "Downvoted!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void get_votes() {
        try {
            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request_votes = new ApiRequest();
            data = new HashMap<>();
            data.put("title", POST_TITLE);
            params = Utiles.getPostDataString(data);
            request_votes.execute("/posts/votes/?" + params, "GET", "", SESSION_KEY).get();
            response_votes = request_votes.getResponse();

            if (request_votes.getSuccess()) {
                Integer likes = response_votes.getInt("LIKES");
                Integer dislikes = response_votes.getInt("DISLIKES");

                txt_likes.setText(String.format("%d", likes));
                txt_dislikes.setText(String.format("%d", dislikes));
            } else {
                Toast.makeText(PostActivity.this, response_votes.getString("error"), Toast.LENGTH_LONG).show();
//                finish();
            }

        } catch (Exception e) {
            Toast.makeText(PostActivity.this, "An error occured: " + e.toString(), Toast.LENGTH_LONG).show();
            request_votes.cancel(true);
            e.printStackTrace();
//            finish();

        }

    }

    public void display_edit_delete_buttons() {
        try {
            request_author = new ApiRequest();
            data = new HashMap<>();
            data.put("title", POST_TITLE);
            params = Utiles.getPostDataString(data);
            request_author.execute("/posts/is_author/?" + params, "GET", "", SESSION_KEY).get();
            response_author = request_author.getResponse();

            System.out.println(request_author);
            System.out.println(response_author);
            if (request_author.getSuccess()) {
                int is_author = response_author.getInt("is_author");

                if (is_author == 1) {
                    bt_edit.setVisibility(View.VISIBLE);
                    bt_delete.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            Toast.makeText(PostActivity.this, "An error occured: " + e.toString(), Toast.LENGTH_LONG).show();
            request_author.cancel(true);
            e.printStackTrace();
//            finish();
        }
    }
}
