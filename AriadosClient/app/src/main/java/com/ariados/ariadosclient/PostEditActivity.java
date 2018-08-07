package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PostEditActivity extends AppCompatActivity {
    Button bt_save;
    Button bt_cancel;
    TextView txt_title;
    EditText input_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        bt_save = findViewById(R.id.bt_save);
        bt_cancel = findViewById(R.id.bt_cancel);
        input_text = findViewById(R.id.input_text);
        txt_title = findViewById(R.id.txt_title);

    }
}
