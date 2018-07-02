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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ariados.ariadosclient.utils.ApiRequest;
import com.ariados.ariadosclient.utils.FallbackLocationTracker;
import com.ariados.ariadosclient.utils.LoginRequest;
import com.ariados.ariadosclient.utils.Session;
import com.ariados.ariadosclient.utils.Utiles;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button bt_continue;
    Button bt_register;
    TextView text_login;
    EditText input_username;
    EditText input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bt_continue = findViewById(R.id.bt_continue);
        bt_register = findViewById(R.id.bt_register);
        text_login = findViewById(R.id.text_welcome);
        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);

        // Solicitamos los permisos para posteriormente usarlos en el registro de la localización actual, en el caso en que no estén dados
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        input_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input_username.getText().toString().isEmpty()) {
                    input_username.setSelection(0, input_username.getText().toString().length());
                }
            }
        });

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_register = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent_register);
            }
        });

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = input_username.getText().toString();
                String password = input_password.getText().toString();
                HashMap<String, String> post_data = new HashMap<>();


                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty username or password", Toast.LENGTH_SHORT).show();
                } else {
                    post_data.put("username", username);
                    post_data.put("password", password);

                    String post_params;
                    Session session = new Session();
                    LoginRequest request = new LoginRequest();
                    ApiRequest update_request = new ApiRequest();

                    try {
                        post_params = Utiles.getPostDataString(post_data);
                        // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
                        request.execute("/auth/login/", post_params).get();
                        session = request.getSession();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    } finally {
                        if (!session.getKey().isEmpty()) {
                            try {
                                HashMap<String, String> get_data = new HashMap<>();
                                Location mLocation = null;
                                FallbackLocationTracker locTracker;

                                double latitud = 0.0;
                                double longitud = 0.0;
                                locTracker = new FallbackLocationTracker(LoginActivity.this);

                                locTracker.start();
                                if (locTracker.hasLocation()) {
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
                                get_data.put("lat", String.valueOf(latitud));
                                get_data.put("lng", String.valueOf(longitud));
                                String params = Utiles.getPostDataString(get_data);

                                update_request.execute("/trainers/update_location/?" + params, "GET", "", session.getKey()).get();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Couldn't get your location", Toast.LENGTH_SHORT).show();
                            }

                            // Cambiar de "layout/activity" al pulsar GO! y que haya funcionado bien
                            Intent intent_main = new Intent(LoginActivity.this, MainActivity.class);
                            intent_main.putExtra("SESSION_KEY", session.getKey());
                            LoginActivity.this.startActivity(intent_main);
                            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }
}
