package com.ariados.ariadosclient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.FallbackLocationTracker;
import com.ariados.ariadosclient.utils.Utiles;

import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Button bt_continue;
    TextView text_register;
    EditText input_username;
    EditText input_password;
    EditText input_email;
    EditText input_name;
    EditText input_home;
    Spinner select_team;
    private String[] teams = new String[]{"INSTINCT", "MYSTIC", "VALOR"};
    FallbackLocationTracker locTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bt_continue = findViewById(R.id.bt_continue);
        text_register = findViewById(R.id.text_welcome);
        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);
        input_email = findViewById(R.id.input_email);
        input_name = findViewById(R.id.input_name);
        input_home = findViewById(R.id.input_home);
        select_team = findViewById(R.id.select_team);
        // Establecemos las opciones para el Spinner -dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                android.R.layout.simple_spinner_item, teams);
        select_team.setAdapter(adapter);

        // Solicitamos los permisos para posteriormente usarlos en el registro de la localización actual, en el caso en que no estén dados
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = input_username.getText().toString();
                String password = input_password.getText().toString();
                String email = input_email.getText().toString();
                String name = input_name.getText().toString();
                String home = input_home.getText().toString();
                String team = select_team.getSelectedItem().toString();

                HashMap<String, String> post_data = new HashMap<>();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || home.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Some required fields are empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Debido a la estructura de registro, los parámetros relativos a la autenticación (user) tendrían la siguiente nomenclatura user.password ...
                    // Recuerda: usa de nombre siempre los que especifica la API
                    post_data.put("user.username", username);
                    post_data.put("user.password", password);
                    post_data.put("user.email", email);
                    post_data.put("name", name);
                    post_data.put("home_location", home);
                    post_data.put("team", team);
                    // Obtenemos la localización actual a través de acceso por GPS
                    Location mLocation = null;
                    double latitud = 0.0;
                    double longitud = 0.0;
                    locTracker = new FallbackLocationTracker(RegisterActivity.this);

                    locTracker.start();
                    if(locTracker.hasLocation()) {
                        mLocation = locTracker.getLocation();
                    } else if (locTracker.hasPossiblyStaleLocation()) {
                        mLocation = locTracker.getPossiblyStaleLocation();
                    } else {
                        locTracker.start();
                    }

                    if (mLocation != null) {
                        latitud = mLocation.getLatitude();
                        longitud = mLocation.getLongitude();
                    }
                    post_data.put("current_location", latitud + "," + longitud);

                    String post_params;
                    ApiRequest request = new ApiRequest();
                    JSONObject response = new JSONObject();
                    Boolean success = false;
                    String message = "";

                    try {
                        post_params = Utiles.getPostDataString(post_data);
                        // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                        request.execute("/trainers/save/", "POST", post_params).get();
                        response = request.getResponse();
                        // Ya que la estructura de respuestas del servidor está así organizada, para saber si ha tenido éxito o no la consulta debemos ver si la
                        // respuesta contiene "error" en el JSON. Si es así, es que ha ocurrido algún error y no podemos proceder.
                        success = !response.has("error");
                        if (!success) {
                            message = response.getString("error");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Username or email already exists", Toast.LENGTH_SHORT).show();
                    } finally {
                        if (!success) {
                            Toast.makeText(getApplicationContext(), "There was an error: " + message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration successfull!", Toast.LENGTH_SHORT).show();
                            // Cambiar de "layout/activity" al pulsar GO! y que haya funcionado bien
                            Intent intent_main = new Intent(RegisterActivity.this, LoginActivity.class);
                            RegisterActivity.this.startActivity(intent_main);
                        }
                    }
                }
            }
        });
    }
}
