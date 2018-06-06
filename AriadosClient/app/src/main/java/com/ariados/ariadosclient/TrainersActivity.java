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
    private ApiRequest request;
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
                    request = new ApiRequest();
                    request.execute("/trainers/filter/?" + params, "GET", "", SESSION_KEY).get();

                    //TODO: encontrar la manera de parsear la respuesta que recibimos si es un ARRAY a JSON y viceversa
                    response = request.getResponse();
                    System.out.println(response.toString());
                } catch (Exception e) {
                    request.cancel(true);
                    e.printStackTrace();
                }
                Toast.makeText(TrainersActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
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
