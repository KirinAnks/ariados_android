package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.ariados.ariadosclient.models.Post;
import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostsActivity extends AppCompatActivity {

    ListView view_list;
    SearchView input_search;
    HashMap<String, String> data = new HashMap<>();
    HashMap<String, String> data_post = new HashMap<>();
    String params;
    String SESSION_KEY;
    private ApiRequest request;
    JSONArray response_array;
    Boolean success;
    String message;
    List<Post> posts;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        input_search = findViewById(R.id.input_search);
        view_list = findViewById(R.id.view_list);
        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        view_list.setAdapter(arrayAdapter);

        response_array = new JSONArray();
        success = false;
        message = "";

        try {
            posts = new ArrayList<>();
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();

            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request = new ApiRequest();
            request.execute("/posts/filter/", "GET", "", SESSION_KEY).get();

            if (request.getSuccess() && request.getIsArray()) {
                response_array = request.getResponseArray();
                ArrayList<JSONObject> json_list = Utiles.castToJSONList(response_array);
                for (JSONObject json : json_list) {
                    posts.add(new Post(json));
                }

                List<String> strings = new ArrayList<>(posts.size());
                for (Post t : posts) {
                    strings.add(t.toString());
                }

                arrayAdapter.addAll(strings);
                arrayAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            request.cancel(true);
            e.printStackTrace();
        }

        input_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private void doJob(String text, boolean submited) {
                try {
                    posts = new ArrayList<>();
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();

                    data.put("title__contains", text);
                    params = Utiles.getPostDataString(data);
                    // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                    request = new ApiRequest();
                    request.execute("/posts/filter/?" + params, "GET", "", SESSION_KEY).get();

                    if (request.getSuccess() && request.getIsArray()) {
                        response_array = request.getResponseArray();
                        ArrayList<JSONObject> json_list = Utiles.castToJSONList(response_array);
                        for (JSONObject json : json_list) {
                            posts.add(new Post(json));
                        }

                        List<String> strings = new ArrayList<>(posts.size());
                        for (Post t : posts) {
                            strings.add(t.toString());
                        }

                        arrayAdapter.addAll(strings);
                        arrayAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    request.cancel(true);
                    e.printStackTrace();
                }
                if (submited)
                    Toast.makeText(PostsActivity.this, posts.size() + " posts found.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    Toast.makeText(PostsActivity.this, "Empty query!", Toast.LENGTH_SHORT).show();
                } else {
                    doJob(query, true);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    doJob(newText, false);
                }
                return true;
            }

        });

    }
}