package com.polware.weatherapp.interfaces;

import com.polware.weatherapp.models.OpenWeatherMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather")
    Call<OpenWeatherMap> getWeatherLocation(@Query("lat") double lat, @Query("lon") double lon
            , @Query("APPID") String app_id, @Query("units") String value, @Query("lang") String lang);

    @GET("weather")
    Call<OpenWeatherMap> getWeatherCity(@Query("q") String city, @Query("APPID") String app_id
            , @Query("units") String value, @Query("lang") String lang);

}
