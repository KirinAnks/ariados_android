package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONObject;

import java.util.HashMap;

public class TrainersActivity extends AppCompatActivity {

    ListView view_list;
    SearchView input_search;
    HashMap<String, String> data = new HashMap<>();
    String params;
    String SESSION_KEY;
    ApiRequest request;
    JSONObject response;
    Boolean success ;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers);

        input_search = findViewById(R.id.input_search);
        view_list = findViewById(R.id.view_list);
        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");

        request = new ApiRequest();
        response = new JSONObject();
        success = false;
        message = "";

        input_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    data.put("name", query);
                    params = Utiles.getPostDataString(data);
                    // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                    request.execute("/trainers/filter/", "GET", params, SESSION_KEY).get();
                    response = request.getResponse();
                    System.out.println(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(TrainersActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                view_list.setAdapter(adapter);
                return true;
            }

        });
    }
}
