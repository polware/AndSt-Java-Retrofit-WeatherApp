package com.polware.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.polware.weatherapp.interfaces.WeatherAPI;
import com.polware.weatherapp.models.OpenWeatherMap;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchCityActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;
    private TextView textViewCitySearch;
    private TextView textViewTemperatureSearch;
    private TextView textViewConditionSearch;
    private TextView textViewHumiditySearch;
    private TextView textViewMaxTempSearch;
    private TextView textViewMinTempSearch;
    private TextView textViewPressureSearch;
    private TextView textViewWindSearch;
    private ImageView imageViewSearch;
    private EditText editTextSearchNewCity;
    private Button buttonSearchNewCity;
    public static String API_KEY = "4ddb4d0fe662aa7d0d5e09176e20fc89";
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Search By City");
        setContentView(R.layout.activity_search_city);

        init();
        Intent intent = getIntent();
        cityName = intent.getStringExtra("city");
        getWeatherData(cityName);
        editTextSearchNewCity.setText("");

        buttonSearchNewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = editTextSearchNewCity.getText().toString();
                getWeatherData(city);
                editTextSearchNewCity.setText("");
            }
        });

    }

    private void init(){
        constraintLayout = findViewById(R.id.constraintLayoutSearch);
        textViewCitySearch = findViewById(R.id.textViewCitySearch);
        textViewTemperatureSearch = findViewById(R.id.textViewTemperatureSearch);
        textViewConditionSearch = findViewById(R.id.textViewConditionSearch);
        textViewHumiditySearch = findViewById(R.id.textViewHumiditySearch);
        textViewMaxTempSearch = findViewById(R.id.textViewMaxTempSearch);
        textViewMinTempSearch = findViewById(R.id.textViewMinTempSearch);
        textViewPressureSearch = findViewById(R.id.textViewPresureSearch);
        textViewWindSearch = findViewById(R.id.textViewWindSearch);
        imageViewSearch = findViewById(R.id.imageViewSearch);
        editTextSearchNewCity = findViewById(R.id.editTextSearchNewCity);
        buttonSearchNewCity = findViewById(R.id.buttonSearchNewCity);
    }

    public void getWeatherData(String city){
        WeatherAPI weatherAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call = weatherAPI.getWeatherCity(city, API_KEY,"metric","es");
        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                if(response.isSuccessful()){
                    OpenWeatherMap openWeatherMap = response.body();
                    textViewCitySearch.setText(new StringBuilder().append(openWeatherMap.getName())
                            .append(" | ").append(openWeatherMap.getSys().getCountry()).toString());
                    textViewTemperatureSearch.setText(new StringBuilder().append(openWeatherMap
                            .getMain().getTemp()).append(" °C").toString());
                    textViewConditionSearch.setText(openWeatherMap.getWeather().get(0).getDescription());
                    textViewHumiditySearch.setText(new StringBuilder().append(openWeatherMap.getMain()
                            .getHumidity()).append("%").toString());
                    textViewMaxTempSearch.setText(new StringBuilder().append(openWeatherMap.getMain()
                            .getTempMax()).append(" °C").toString());
                    textViewMinTempSearch.setText(new StringBuilder().append(openWeatherMap.getMain()
                            .getTempMin()).append(" °C").toString());
                    String pressure = String.valueOf(openWeatherMap.getMain().getPressure());
                    textViewPressureSearch.setText(pressure);
                    String speed = String.valueOf(openWeatherMap.getWind().getSpeed());
                    textViewWindSearch.setText(speed);
                    String iconCode = openWeatherMap.getWeather().get(0).getIcon();
                    Picasso.get().load("https://openweathermap.org/img/wn/"+iconCode+"@2x.png")
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imageViewSearch);
                }
                else {
                    Snackbar.make(constraintLayout, "City not found", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
                Toast.makeText(SearchCityActivity.this, "ERROR: "+t.getMessage()
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

}