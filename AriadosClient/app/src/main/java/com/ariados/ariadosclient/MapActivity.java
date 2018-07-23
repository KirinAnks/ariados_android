package com.ariados.ariadosclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariados.ariadosclient.models.Trainer;
import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.FallbackLocationTracker;
import com.ariados.ariadosclient.utils.Utiles;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    MapView map_location;
    GoogleMap map;
    String SESSION_KEY;
    ApiRequest request;
    JSONArray response_array;
    JSONObject response;
    List<Trainer> trainers = new ArrayList<>();
    HashMap<String, String> data;
    LatLng centro = new LatLng(0.0, 0.0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Solicitamos los permisos para posteriormente usarlos en el registro de la localización actual, en el caso en que no estén dados
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        map_location = findViewById(R.id.map_location);
        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");

        // Obtenemos el centro del mapa que será la localización actual:
        Location mLocation = null;
        FallbackLocationTracker locTracker;

        double latitud_centro;
        double longitud_centro;
        locTracker = new FallbackLocationTracker(MapActivity.this);

        locTracker.start();
        if (locTracker.hasLocation()) {
            mLocation = locTracker.getLocation();
        } else if (locTracker.hasPossiblyStaleLocation()) {
            mLocation = locTracker.getPossiblyStaleLocation();
        } else {
            locTracker.start();
        }

        if (mLocation != null) {
            latitud_centro = mLocation.getLatitude();
            longitud_centro = mLocation.getLongitude();
            centro = new LatLng(latitud_centro, longitud_centro);
        }

        // Snippet de codigo para establecer la localización en el mapa
        map_location.onCreate(savedInstanceState);
        map_location.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Para que aparezcan los datos a mostrar así como el mapa hay que consultar a la api
                try {
                    // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                    request = new ApiRequest();
                    request.execute("/trainers/get_closest_trainers/", "GET", "", SESSION_KEY).get();

                    if (request.getSuccess() && request.getIsArray()) {
                        response_array = request.getResponseArray();
                        ArrayList<JSONObject> json_list = Utiles.castToJSONList(response_array);
                        for(JSONObject json: json_list){
                            trainers.add(new Trainer(json));
                        }

                        for (Trainer t: trainers){
                            String[] latlong = t.getCurrent_location().split(",");
                            double lat,lng;
                            lat = lng = 0.0;
                            if(latlong.length == 2){
                                lat = Double.parseDouble(t.getCurrent_location().split(",")[0]);
                                lng = Double.parseDouble(t.getCurrent_location().split(",")[1]);
                            }
                            LatLng coordinates = new LatLng(lat, lng);
                            googleMap.addMarker(new MarkerOptions().position(coordinates).title(t.getName()));

                        }

                    } else {
                        response = request.getResponse();
                        Toast.makeText(MapActivity.this, response.getString("error"), Toast.LENGTH_LONG).show();
                        request.cancel(true);
                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centro, 18));
                    map_location.onResume();

                } catch (Exception e) {
                    Toast.makeText(MapActivity.this, "An error occured: " + e.toString(), Toast.LENGTH_LONG).show();
                    request.cancel(true);
                    e.printStackTrace();
                }

            }
        });
    }
}
