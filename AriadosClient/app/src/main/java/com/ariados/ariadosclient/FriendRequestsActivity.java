package com.ariados.ariadosclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariados.ariadosclient.models.FriendRequest;
import com.ariados.ariadosclient.models.Trainer;
import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendRequestsActivity extends AppCompatActivity {

    ListView view_list;
    HashMap<String, String> data_friend_request = new HashMap<>();
    String SESSION_KEY;
    ApiRequest request;
    JSONArray response_array;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<FriendRequest> friend_requests;
    String params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendrequests);

        view_list = findViewById(R.id.view_list);
        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 );
        view_list.setAdapter(arrayAdapter);

        response_array = new JSONArray();

        try {
            friend_requests = new ArrayList<>();
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();

            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request = new ApiRequest();
            request.execute("/trainers/get_friend_requests/", "GET", "", SESSION_KEY).get();

            if (request.getSuccess() && request.getIsArray()) {
                response_array = request.getResponseArray();
                ArrayList<JSONObject> json_list = Utiles.castToJSONList(response_array);
                for(JSONObject json: json_list){
                    friend_requests.add(new FriendRequest(json));
                }

                List<String> strings = new ArrayList<>(friend_requests.size());
                for (FriendRequest fr : friend_requests) {
                    strings.add(fr.toString());
                }

                arrayAdapter.addAll(strings);
                arrayAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            request.cancel(true);
            e.printStackTrace();
        }

        view_list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friend_request = arrayAdapter.getItem(position);
                String trainer_name =  friend_request.split("\\s+")[0];
                data_friend_request.put("trainer_name", trainer_name);

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(FriendRequestsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(FriendRequestsActivity.this );
                }
                builder.setTitle("Friend request")
                        .setMessage(trainer_name + "wants to be your friend. Do you want to add " + trainer_name + " as a new friend?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    params = Utiles.getPostDataString(data_friend_request);
                                    // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                                    request = new ApiRequest();
                                    request.execute("/trainers/accept_friend_request_from/?" + params, "GET", "", SESSION_KEY).get();

                                    if (request.getSuccess()) {
                                        JSONObject result = request.getResponse();
                                        if(result.has("success")){
                                            Toast.makeText(FriendRequestsActivity.this, result.getString("success"), Toast.LENGTH_LONG).show();
                                        }else if(result.has("error")){
                                            Toast.makeText(FriendRequestsActivity.this, result.getString("error"), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    request.cancel(true);
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    params = Utiles.getPostDataString(data_friend_request);
                                    // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                                    request = new ApiRequest();
                                    request.execute("/trainers/reject_friend_request_from/?" + params, "GET", "", SESSION_KEY).get();

                                    if (request.getSuccess()) {
                                        JSONObject result = request.getResponse();
                                        if(result.has("success")){
                                            Toast.makeText(FriendRequestsActivity.this, result.getString("success"), Toast.LENGTH_LONG).show();
                                        }else if(result.has("error")){
                                            Toast.makeText(FriendRequestsActivity.this, result.getString("error"), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    request.cancel(true);
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_add)
                        .show();
            }

        });
    }
}
