package com.ariados.ariadosclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button bt_search;
    TextView text_welcome;
    TextView text_key;
    String SESSION_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_search = findViewById(R.id.bt_search);
        text_welcome = findViewById(R.id.text_welcome);
        text_key = findViewById(R.id.text_key);

        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        text_key.setText(SESSION_KEY);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar de "layout/activity" al pulsar este botón
                Intent intent_trainers = new Intent(MainActivity.this, TrainersActivity.class);
                intent_trainers.putExtra("SESSION_KEY", SESSION_KEY);
                MainActivity.this.startActivity(intent_trainers);
            }
        });
    }
}
