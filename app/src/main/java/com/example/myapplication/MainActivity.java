package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView cityName;
    Button searchButton;
    TextView result;

    public void search(View view) {
        cityName = findViewById(R.id.cityNameTextId);
        searchButton = findViewById(R.id.searchButtonId);
        result = findViewById(R.id.resulttextViewId);

        String cName = cityName.getText().toString();

        String content;
        Weather weather = new Weather();
        try {
            content = weather.execute("https://api.openweathermap.org/data/2.5/weather?q=" +
                    cName + "&units=imperial&appid=21859dbe63e62266debf234a26139065").get();
            //first check data and that it is retrievable or not
            Log.i("content", content);

            //JSON
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String mainTemperature = jsonObject.getString("main"); //this is not part of weather array, it is a separate variable
            double visibility;

            //Log.i("weatherData",weatherData);
            //weather data is in Array
            JSONArray array = new JSONArray(weatherData);

            String main = "";
            String description = "";
            String temperature = "";

            for (int i = 0; i < array.length(); i++) {
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
            }

            JSONObject mainPart = new JSONObject(mainTemperature);
            temperature = mainPart.getString("temp");

            visibility = Double.parseDouble(jsonObject.getString("visibility"));
            //Visibility is measured in meter(s) by default
            int visibilityInKilometer = (int) visibility / 1000;

            Log.i("Temperature", temperature);

            /*Log.i("main", main);
            Log.i("description", description);*/

            String resultText = "Weather :     " + main +
                    "\nDescription :    " + description +
                    "\nTemperature :    " + temperature + " F" +
                    "\nVisibility :     " + visibilityInKilometer + " KM";

            result.setText(resultText);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    class Weather extends AsyncTask<String, Void, String> {   //First String means URL is in String, Void means nothing, Third string means Return type will be String

        @Override
        protected String doInBackground(String... address) {

            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Establish connection with address
                connection.connect();

                //retrieve data from url
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                //Retrieve data and return it as String
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1) {
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}