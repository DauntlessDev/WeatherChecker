package com.dauntlessdev.weatherchecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Toast toast;

    public void weatherCheck(View view){
        TextView input = findViewById(R.id.editText);
        DownloaderTask downloaderTask = new DownloaderTask();
        try {
            String encodedString = URLEncoder.encode(input.getText().toString(),"UTF-8");
            downloaderTask.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedString + "&appid=b6907d289e10d714a6e88b30761fae22");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        InputMethodManager mng = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mng.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }
    public class DownloaderTask extends AsyncTask<String,Void, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Can't Find the Weather", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Can't Find the Weather", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return "Failed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView result = findViewById(R.id.resultView);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(weatherInfo);
                String msg = "";
                for (int i=0; i< jsonArray.length(); i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String des = jsonPart.getString("description");
                    if (!main.isEmpty() && !des.isEmpty()){
                        msg += main + ": " + des + "\n";

                    }
                }
                if (!msg.isEmpty()){
                    result.setText(msg);
                }else{
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


}
