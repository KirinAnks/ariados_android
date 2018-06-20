package com.ariados.ariadosclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.ariados.ariadosclient.models.Trainer;
import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainersActivity extends AppCompatActivity {

    ListView view_list;
    SearchView input_search;
    HashMap<String, String> data = new HashMap<>();
    HashMap<String, String> data_friend_request = new HashMap<>();
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

        view_list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String trainer = arrayAdapter.getItem(position);
                String trainer_name =  trainer.split("\\s+")[0];
                data_friend_request.put("trainer_name", trainer_name);

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(TrainersActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(TrainersActivity.this );
                }
                builder.setTitle("Friend request")
                        .setMessage(trainer_name + "is not your friend. Do you want to add " + trainer_name + " as a new friend?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    params = Utiles.getPostDataString(data_friend_request);
                                    // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                                    request = new ApiRequest();
                                    request.execute("/trainers/send_friend_request/?" + params, "GET", "", SESSION_KEY).get();

                                    if (request.getSuccess()) {
                                        JSONObject result = request.getResponse();
                                        if(result.has("success")){
                                            Toast.makeText(TrainersActivity.this, result.getString("success"), Toast.LENGTH_LONG).show();
                                        }else if(result.has("error")){
                                            Toast.makeText(TrainersActivity.this, result.getString("error"), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    request.cancel(true);
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_add)
                        .show();
            }

        });
    }
}
