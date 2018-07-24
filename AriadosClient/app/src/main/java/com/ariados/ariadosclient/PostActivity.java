package com.ariados.ariadosclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariados.ariadosclient.models.Post;
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

public class PostActivity extends AppCompatActivity {

    TextView txt_title;
    TextView txt_text;
    TextView txt_metadata;
    TextView txt_likes;
    TextView txt_dislikes;
    String SESSION_KEY;
    String POST_TITLE;
    ImageView img_like;
    ImageView img_dislike;
    ApiRequest request;
    JSONObject response;
    Post post = new Post();
    HashMap<String, String> data;
    String params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        txt_title = findViewById(R.id.txt_title);
        txt_text = findViewById(R.id.txt_text);
        txt_metadata = findViewById(R.id.txt_metadata);
        txt_likes = findViewById(R.id.txt_likes);
        txt_dislikes = findViewById(R.id.txt_dislikes);
        txt_title = findViewById(R.id.txt_title);
        img_like = findViewById(R.id.img_like);
        img_dislike = findViewById(R.id.img_dislike);

        SESSION_KEY = getIntent().getStringExtra("SESSION_KEY");
        POST_TITLE = getIntent().getStringExtra("POST_TITLE");

        // Para que aparezcan los datos a mostrar hay que consultar a la api
        try {
            // Hacemos la llamada asíncrona con execute, pero mediante .get() rompemos la asincronía para poder obtener los valores seteados.
            request = new ApiRequest();
            data = new HashMap<>();
            data.put("title", POST_TITLE);
            params = Utiles.getPostDataString(data);
            request.execute("/posts/get/?" + params, "GET", "", SESSION_KEY).get();
            response = request.getResponse();

            if (request.getSuccess()) {
                post = new Post(response);

                txt_title.setText(post.getTitle());
                txt_text.setText(post.getText());
                txt_metadata.setText(String.format("posted by %s at %s", post.getCreator(), post.getLast_update()));
                
            } else {
                Toast.makeText(PostActivity.this, response.getString("error"), Toast.LENGTH_LONG).show();
//                finish();
            }

        } catch (Exception e) {
            Toast.makeText(PostActivity.this, "An error occured: " + e.toString(), Toast.LENGTH_LONG).show();
            request.cancel(true);
            e.printStackTrace();
//            finish();

        }
    }
}
