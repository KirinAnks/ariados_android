package com.ariados.ariadosclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                            // Cambiar de "layout/activity" al pulsar GO! y que haya funcionado bien
                            Intent intent_main = new Intent(LoginActivity.this, MainActivity.class);
                            intent_main.putExtra("SESSION_KEY", session.getKey());
                            LoginActivity.this.startActivity(intent_main);
                            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }
}
