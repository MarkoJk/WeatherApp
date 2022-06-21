package com.test.weather.data

import com.test.weather.data.entities.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<Weather?>

    @GET("weather")
    fun getWeather(@Query("q") cityName: String): Call<Weather?>
}
