package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button bt_search;
    TextView text_welcome;
    TextView text_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_search = findViewById(R.id.bt_search);
        text_welcome = findViewById(R.id.text_welcome);
        text_key = findViewById(R.id.text_key);

        text_key.setText(getIntent().getStringExtra("SESSION_KEY"));
    }
}
