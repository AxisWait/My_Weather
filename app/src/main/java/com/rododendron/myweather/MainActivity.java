package com.rododendron.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=9fc4fa6351a85c617e109559230aa9fc&lang=ru&units=metric";
    private EditText editTextCityOrIndex;
   // private Button buttonRequest;
    private TextView textViewWeatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCityOrIndex = findViewById(R.id.editTextCityOrIndex);
        textViewWeatherInfo = findViewById(R.id.textViewWeatherInfo);
    }

    public void onClickWeatherRequest(View view) {
        String city = editTextCityOrIndex.getText().toString().trim();
        if(!city.isEmpty()){
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHER_URL, city);
            task.execute(url);
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;
            StringBuilder stringBuilderWeather = new StringBuilder();

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line !=null){
                    stringBuilderWeather.append(line);
                    line = bufferedReader.readLine();
                }
                return stringBuilderWeather.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                Float tempFloat = Float.valueOf(temp);
                int tempInt = (int)Math.round(tempFloat);
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format("%s\nТемпература: %s C˚\nНа улице: %s", city,tempInt,description);
                textViewWeatherInfo.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
