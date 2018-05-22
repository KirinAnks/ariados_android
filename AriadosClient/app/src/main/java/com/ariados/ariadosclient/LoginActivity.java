package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button bt_continue;
    TextView text_login;
    TextView text_error;
    EditText input_username;
    EditText input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bt_continue = findViewById(R.id.bt_continue);
        text_login = findViewById(R.id.text_login);
        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);
        text_error = findViewById(R.id.text_error);

        text_error.setText("");

        input_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input_username.getText().toString().isEmpty()) {
                    input_username.setSelection(0, input_username.getText().toString().length());
                }
            }
        });

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_login.setText("Logging in...");
                String username = input_username.getText().toString();
                String password = input_password.getText().toString();
                HashMap<String, String> post_data = new HashMap<>();

                if (username.isEmpty() || password.isEmpty()) {
                    text_error.setText("Empty username or password");
                } else {
                    text_error.setText("");

                    post_data.put("username", username);
                    post_data.put("password", password);

                    String post_params;
                    String result = "Did not work.";

                    try {
                        post_params = Utiles.getPostDataString(post_data);
                        result = new APICall().execute("/auth/login/", post_params).get();

                    } catch (Exception e) {
                        e.printStackTrace();
                        text_error.setText("Error: invalid username or password.");
                    } finally {
                        text_error.setText(result);
                        text_login.setText("Finished");
                    }

                }
            }
        });

    }
}
