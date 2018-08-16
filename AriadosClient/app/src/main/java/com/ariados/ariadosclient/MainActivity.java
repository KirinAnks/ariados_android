package com.ariados.ariadosclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ariados.ariadosclient.models.Event;
import com.ariados.ariadosclient.models.FriendRequest;
import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button bt_search;
    Button bt_friends;
    Button bt_map;
    Button bt_posts;
    Button bt_logout;
    TextView text_welcome;
    TextView txt_requests;
    String SESSION_KEY;
    Button bt_requests;
    ApiRequest request;
    ApiRequest request_events;
    ArrayList<FriendRequest> friend_requests;
    ArrayList<Event> events;
    JSONArray response_array;
    JSONArray events_response_array;
    ListView list_events;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_search = findViewById(R.id.bt_search);
        bt_posts = findViewById(R.id.bt_posts);
        bt_map = findViewById(R.id.bt_map);
        bt_friends = findViewById(R.id.bt_friends);
        bt_logout = findViewById(R.id.bt_logout);
        bt_requests = findViewById(R.id.bt_requests);
        text_welcome = findViewById(R.id.text_welcome);
        txt_requests = findViewById(R.id.txt_requests);

        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");

        // Button listeners
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_trainers = new Intent(MainActivity.this, TrainersActivity.class);
                intent_trainers.putExtra("SESSION_KEY", SESSION_KEY);
                MainActivity.this.startActivity(intent_trainers);
            }
        });

        bt_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_friends = new Intent(MainActivity.this, FriendsActivity.class);
                intent_friends.putExtra("SESSION_KEY", SESSION_KEY);
                MainActivity.this.startActivity(intent_friends);
            }
        });
        // el listener de onclick del botón de las friend requests
        bt_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_requests = new Intent(MainActivity.this, FriendRequestsActivity.class);
                intent_requests.putExtra("SESSION_KEY", SESSION_KEY);
                MainActivity.this.startActivity(intent_requests);
            }
        });

        // el listener de onclick del botón del mapa
        bt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_map = new Intent(MainActivity.this, MapActivity.class);
                intent_map.putExtra("SESSION_KEY", SESSION_KEY);
                MainActivity.this.startActivity(intent_map);
            }
        });

        // el listener de onclick del botón del mapa
        bt_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_posts = new Intent(MainActivity.this, PostsActivity.class);
                intent_posts.putExtra("SESSION_KEY", SESSION_KEY);
                MainActivity.this.startActivity(intent_posts);
            }
        });

        // el listener de onclick del botón de logout
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_posts = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent_posts);
            }
        });


        // Para que aparezca el texto y el botón de las friend request en el caso en que existan
        try {
            friend_requests = new ArrayList<>();

            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request = new ApiRequest();
            request.execute("/trainers/get_friend_requests/", "GET", "", SESSION_KEY).get();

            if (request.getSuccess() && request.getIsArray()) {
                response_array = request.getResponseArray();
                ArrayList<JSONObject> json_list = Utiles.castToJSONList(response_array);
//                for(JSONObject json: json_list){
//                    friend_requests.add(new FriendRequest(json));
//                }
                if (json_list.size() > 0) {
                    bt_requests.setVisibility(View.VISIBLE);
                    txt_requests.setVisibility(View.VISIBLE);
                    txt_requests.setText("You have " + json_list.size() + " pending friend requests");
                }
            }

        } catch (Exception e) {
            request.cancel(true);
            e.printStackTrace();
        }


        // EVENTS LIST GETTER
        list_events = findViewById(R.id.list_events);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        list_events.setAdapter(arrayAdapter);

        events_response_array = new JSONArray();

        try {
            events = new ArrayList<>();
            arrayAdapter.clear();
            arrayAdapter.notifyDataSetChanged();

            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request_events = new ApiRequest();
            request_events.execute("/events/", "GET", "", SESSION_KEY).get();

            if (request_events.getSuccess() && request_events.getIsArray()) {
                events_response_array = request_events.getResponseArray();
                ArrayList<JSONObject> json_list = Utiles.castToJSONList(events_response_array);
                for (JSONObject json : json_list) {
                    events.add(new Event(json));
                }

                List<String> strings = new ArrayList<>(events.size());
                for (Event e : events) {
                    strings.add(e.toString());
                }

                arrayAdapter.addAll(strings);
                arrayAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            request_events.cancel(true);
            e.printStackTrace();
        }
    }
}
