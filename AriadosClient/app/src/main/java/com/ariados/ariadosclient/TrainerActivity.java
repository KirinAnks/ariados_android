package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariados.ariadosclient.models.Trainer;
import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.Utiles;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;

public class TrainerActivity extends AppCompatActivity {

    TextView txt_name;
    TextView txt_home;
    MapView map_location;
    GoogleMap map;
    String SESSION_KEY;
    String TRAINER_NAME;
    ImageView img_instinct;
    ImageView img_mystic;
    ImageView img_valor;
    ApiRequest request;
    JSONObject response;
    Trainer trainer = new Trainer();
    HashMap<String, String> data;
    String params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer);

        txt_name = findViewById(R.id.txt_title);
        txt_home = findViewById(R.id.txt_home);
        map_location = findViewById(R.id.map_location);
        img_instinct = findViewById(R.id.img_instinct);
        img_mystic = findViewById(R.id.img_mystic);
        img_valor = findViewById(R.id.img_valor);

        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        TRAINER_NAME = getIntent().getStringExtra("TRAINER_NAME");

        // Para que aparezcan los datos a mostrar así como el mapa hay que consultar a la api
        try {
            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request = new ApiRequest();
            data = new HashMap<>();
            data.put("name", TRAINER_NAME);
            params = Utiles.getPostDataString(data);
            request.execute("/trainers/get/?" + params, "GET", "", SESSION_KEY).get();
            response = request.getResponse();

            if (request.getSuccess()) {
                trainer = new Trainer(response);

                txt_name.setText(trainer.getName());
                txt_home.setText(trainer.getHome_location());

                switch (trainer.getTeam()) {
                    case "INSTINCT":
                        img_instinct.setVisibility(View.VISIBLE);
                        break;
                    case "MYSTIC":
                        img_mystic.setVisibility(View.VISIBLE);
                        break;
                    case "VALOR":
                        img_valor.setVisibility(View.VISIBLE);
                        break;
                    default:
                        img_valor.setVisibility(View.VISIBLE);
                        break;
                }
            } else {
                Toast.makeText(TrainerActivity.this, response.getString("error"), Toast.LENGTH_LONG).show();
//                finish();
            }

        } catch (Exception e) {
            Toast.makeText(TrainerActivity.this, "An error occured: " + e.toString(), Toast.LENGTH_LONG).show();
            request.cancel(true);
            e.printStackTrace();
//            finish();

        }

        // Snippet de codigo para establecer la localización en el mapa
        map_location.onCreate(savedInstanceState);
        map_location.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                String[] latlong = trainer.getCurrent_location().split(",");
                double lat,lng;
                lat = lng = 0.0;
                if(latlong.length == 2){
                    lat = Double.parseDouble(trainer.getCurrent_location().split(",")[0]);
                    lng = Double.parseDouble(trainer.getCurrent_location().split(",")[1]);
                }
                LatLng coordinates = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(coordinates).title(trainer.getName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                map_location.onResume();
            }
        });
    }
}
