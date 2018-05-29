package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    Button bt_continue;
    TextView text_register;
    EditText input_username;
    EditText input_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bt_continue = findViewById(R.id.bt_continue);
        text_register = findViewById(R.id.text_welcome);
        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
