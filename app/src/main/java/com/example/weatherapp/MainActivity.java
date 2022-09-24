package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.tv.TvContract;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, tempIV, conditionIV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;

    private ArrayList<WeatherItem> items = new ArrayList<>();
    private WeatherIconAdapter adapter;

    private String url = "http://api.openweathermap.org/data/2.5/weather";
    private String urlF = "http://api.openweathermap.org/data/2.5/forecast";
    private String appid = "207db3a0adcd44928b7bc04b9bd3f82e";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        tempIV = findViewById(R.id.idTVTemperature);
        conditionIV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherDetails(v);
                getForecastDetails(v);
            }
        });
    }

    public void getWeatherDetails(View view){
        String tempUrl = "";
        String city = cityEdt.getText().toString().trim();
        if(city.isEmpty()){
            cityEdt.setError("Cannot be empty");
            cityEdt.requestFocus();
            return;
        }

        tempUrl = url + "?q=" + city + "&appid=" + appid + "&units=metric";

        StringRequest stringReq = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("Response", response);
                String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsarray = jsonResponse.getJSONArray("weather");
                    JSONObject jsObjWeather = jsarray.getJSONObject(0);
                    String conditions = jsObjWeather.getString("main");

                    JSONObject jsobjmain = jsonResponse.getJSONObject("main");
                    double temp = jsobjmain.getDouble("temp");

                    cityNameTV.setText(city);
                    tempIV.setText(String.valueOf((int)temp)+"°C");
                    conditionIV.setText(conditions);
                    switch (conditions){
                        case "Clouds":
                            iconIV.setImageResource(R.drawable.cloud_big);
                            loadBackground("https://images.unsplash.com/photo-1534088568595-a066f410bcda?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MTB8fGNsb3VkeSUyMHNreXxlbnwwfHwwfHw%3D&w=1000&q=80");
                            break;
                        case "Rain":
                            iconIV.setImageResource(R.drawable.rainy_big);
                            loadBackground("https://i.pinimg.com/736x/66/f7/d5/66f7d5aaea8b44c6d10244dce9f36e7f.jpg");
                            break;
                        default: {
                            iconIV.setImageResource(R.drawable.sun_big);
                            loadBackground("https://i.pinimg.com/originals/b1/ec/43/b1ec430ab76b54a6025055f94f6d7ec9.jpg");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringReq);
    }

    public void loadBackground(String url){
        Glide.with(this).load(url).into(backIV);
    }

    public void getForecastDetails(View view){
        String tempUrl = "";
        String city = cityEdt.getText().toString().trim();
        if(city.isEmpty()){
            cityEdt.setError("Cannot be empty");
            cityEdt.requestFocus();
            return;
        }

        tempUrl = urlF + "?q=" + city + "&appid=" + appid + "&units=metric";

        StringRequest stringReq = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";
                try {
                    JSONObject jsresponse = new JSONObject(response);
                    JSONArray list = jsresponse.getJSONArray("list");
                    JSONObject forecast1 = list.getJSONObject(0);
                    JSONObject forecast2 = list.getJSONObject(1);
                    JSONObject forecast3 = list.getJSONObject(2);
                    JSONObject forecast4 = list.getJSONObject(3);
                    items = new ArrayList<>();
                    addWeatherItem(getHour(forecast1),getTemp(forecast1), getConditions(forecast1));
                    addWeatherItem(getHour(forecast2),getTemp(forecast2), getConditions(forecast2));
                    addWeatherItem(getHour(forecast3),getTemp(forecast3), getConditions(forecast3));
                    addWeatherItem(getHour(forecast4),getTemp(forecast4), getConditions(forecast4));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringReq);
    }

    public void addWeatherItem(String hour, String temp, String condition){
        items.add(new WeatherItem(hour,temp,condition));
        adapter = new WeatherIconAdapter(this);
        adapter.setItems(items);
        weatherRV.setAdapter(adapter);
        weatherRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
    }

    public String getHour(JSONObject obj) throws JSONException {
        String time = obj.getString("dt_txt");
        String hour = time.substring(11,16);
        return hour;
    }

    public String getTemp(JSONObject obj) throws JSONException {
        JSONObject main = obj.getJSONObject("main");
        double temp = main.getDouble("temp");
        return String.valueOf((int)temp)+"°C";
    }

    public String getConditions(JSONObject obj) throws JSONException{
        JSONArray weather = obj.getJSONArray("weather");
        JSONObject internObj = weather.getJSONObject(0);
        String condition = internObj.getString("main");
        return condition;
    }
}