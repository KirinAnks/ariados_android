package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.ariados.ariadosclient.models.Trainer;
import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TrainersActivity extends AppCompatActivity {

    ListView view_list;
    SearchView input_search;
    HashMap<String, String> data = new HashMap<>();
    String params;
    String SESSION_KEY;
    private ApiRequest request;
    JSONArray response_array;
    Boolean success;
    String message;
    List<Trainer> trainers;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers);

        input_search = findViewById(R.id.input_search);
        view_list = findViewById(R.id.view_list);
        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 );
        view_list.setAdapter(arrayAdapter);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).
        response_array = new JSONArray();
        success = false;
        message = "";

        input_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private void doJob(String text, boolean submited){
                try {
                    trainers = new ArrayList<>();
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();

                    data.put("name__contains", text);
                    params = Utiles.getPostDataString(data);
                    // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                    request = new ApiRequest();
                    request.execute("/trainers/filter/?" + params, "GET", "", SESSION_KEY).get();

                    if (request.getSuccess()) {
                        response_array = request.getResponseArray();
                        ArrayList<JSONObject> json_list = Utiles.castToJSONList(response_array);
                        for(JSONObject json: json_list){
                            trainers.add(new Trainer(json));
                        }

                        List<String> strings = new ArrayList<>(trainers.size());
                        for (Trainer t : trainers) {
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
                    Toast.makeText(TrainersActivity.this, trainers.size() + " trainers found.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.isEmpty()){
                    Toast.makeText(TrainersActivity.this, "Empty query!", Toast.LENGTH_SHORT).show();
                } else{
                    doJob(query, true);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    doJob(newText, false);
                }
                return true;
            }

        });
    }
}
