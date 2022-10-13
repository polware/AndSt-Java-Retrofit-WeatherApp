package com.polware.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.polware.weatherapp.interfaces.WeatherAPI;
import com.polware.weatherapp.models.OpenWeatherMap;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// https://api.openweathermap.org/data/2.5/weather?lat=2.97379&lon=-75.28270&appid=4ddb4d0fe662aa7d0d5e09176e20fc89&units=metric
// https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}&units=metric

public class MainActivity extends AppCompatActivity {
    private TextView textViewCity;
    private TextView textViewTemperature;
    private TextView textViewCondition;
    private TextView textViewHumidity;
    private TextView textViewMaxTemp;
    private TextView textViewMinTemp;
    private TextView textViewPressure;
    private TextView textViewWind;
    private EditText editTextSearch;
    private ImageView imageViewMain;
    private Button buttonSearch;
    public static String API_KEY = "4ddb4d0fe662aa7d0d5e09176e20fc89";
    LocationManager locationManager;
    LocationListener locationListener;
    private String cityName;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityName = editTextSearch.getText().toString();
                Intent intent = new Intent(MainActivity.this, SearchCityActivity.class);
                intent.putExtra("city", cityName);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.e("Latitude ", String.valueOf(latitude));
                Log.e("Longitude ", String.valueOf(longitude));
                getWeatherData(latitude, longitude);
            }
        };
        requestPermissionLocation();

    }

    private void init(){
        textViewCity = findViewById(R.id.textViewCityMain);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewCondition = findViewById(R.id.textViewCondition);
        textViewHumidity = findViewById(R.id.textViewHumidity);
        textViewMaxTemp = findViewById(R.id.textViewMaxTemp);
        textViewMinTemp = findViewById(R.id.textViewMinTemp);
        textViewPressure = findViewById(R.id.textViewPressure);
        textViewWind = findViewById(R.id.textViewWind);
        imageViewMain = findViewById(R.id.imageViewMain);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
    }

    private void requestPermissionLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION}, 1);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400,
                    50, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && permissions.length > 0 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400,
                    50, locationListener);
        }
    }

    public void getWeatherData(double lat, double lon){
        WeatherAPI weatherAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call = weatherAPI.getWeatherLocation(lat, lon, API_KEY,"metric","es");
        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                OpenWeatherMap openWeatherMap = response.body();
                textViewCity.setText(new StringBuilder().append(openWeatherMap.getName())
                        .append(" | ").append(openWeatherMap.getSys().getCountry()).toString());
                textViewTemperature.setText(new StringBuilder().append(openWeatherMap.getMain().getTemp()).append(" °C").toString());
                textViewCondition.setText(openWeatherMap.getWeather().get(0).getDescription());
                textViewHumidity.setText(new StringBuilder().append(openWeatherMap.getMain().getHumidity()).append("%").toString());
                textViewMaxTemp.setText(new StringBuilder().append(openWeatherMap.getMain().getTempMax()).append(" °C").toString());
                textViewMinTemp.setText(new StringBuilder().append(openWeatherMap.getMain().getTempMin()).append(" °C").toString());
                String pressure = String.valueOf(openWeatherMap.getMain().getPressure());
                textViewPressure.setText(pressure);
                String speed = String.valueOf(openWeatherMap.getWind().getSpeed());
                textViewWind.setText(speed);
                String iconCode = openWeatherMap.getWeather().get(0).getIcon();
                Picasso.get().load("https://openweathermap.org/img/wn/"+iconCode+"@2x.png")
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imageViewMain);
            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR: "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}