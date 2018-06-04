package com.ariados.ariadosclient.utils;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ApiRequest extends AsyncTask<String, String, String> {
    //    private static final String SERVER_URL = "http://192.168.72.3:8000";
    private static final String SERVER_URL = "http://192.168.100.38:8000";
    private JSONObject response;

    public ApiRequest() {
        this.response = new JSONObject();
    }

    public JSONObject getResponse() {
        return response;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder result = new StringBuilder();
        try {
            OutputStream out = null;

            URL url = new URL(SERVER_URL + params[0]);
            String method = params[1];
            String data = params[2];
            String key = "";
            if (params.length > 3)
                key = params[3];

//          Iniciando la conexión url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setConnectTimeout(10000);
            if (params.length > 3)
                urlConnection.setRequestProperty("Authorization", "Token " + key);

//          Buffer de salida para inyectar los parámetros POST para la conexión
            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();
            urlConnection.connect();

//          Buffer de entrada para escribir la respuesta de la conexión
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            // Seteamos los atributos para posteriormente recuperarlos (aunque rompemos la llamada asíncrona con el .get())
            try {
                this.response = new JSONObject(result.toString());
            } catch (Exception e) {
                this.response = new JSONObject();
            }

            return result.toString();
        } catch (Exception e) {
            return "There was an error: " + e.toString() + "; " + e.getMessage();
        }

    }

    @Override
    protected void onPostExecute(String result) {
        try {
            this.response = new JSONObject(result);
        } catch (Exception e) {
            this.response = null;
        }
    }
}
