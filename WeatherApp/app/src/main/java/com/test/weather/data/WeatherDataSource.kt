package com.test.weather.data

import com.test.weather.data.entities.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class WeatherDataSource @Inject constructor(
    private val weatherService: WeatherService
) {
    fun getWeather(lat: Double, lon: Double, responseCallback: (weather: Weather?) -> Unit) {
        weatherService.getWeather(lat, lon).enqueue(object : Callback<Weather?> {
            override fun onResponse(call: Call<Weather?>, response: Response<Weather?>) {
                var weather: Weather? = null

                if (response.isSuccessful && response.body() != null) {
                    weather = response.body()
                }
                responseCallback.invoke(weather)
            }

            override fun onFailure(call: Call<Weather?>, t: Throwable) {
                responseCallback.invoke(null)
            }
        })
    }

    fun getWeather(cityName: String, responseCallback: (weather: Weather?) -> Unit) {
        weatherService.getWeather(cityName).enqueue(object : Callback<Weather?> {
            override fun onResponse(call: Call<Weather?>, response: Response<Weather?>) {
                var weather: Weather? = null

                if (response.isSuccessful && response.body() != null) {
                    weather = response.body()
                }
                responseCallback.invoke(weather)
            }

            override fun onFailure(call: Call<Weather?>, t: Throwable) {
                responseCallback.invoke(null)
            }
        })
    }
}
