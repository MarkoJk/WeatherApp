package com.test.weather.data.entities

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("weather")
    val info: List<WeatherInfo>,
    @SerializedName("main")
    val tempInfo: TempInfo,
    @SerializedName("wind")
    val windInfo: WindInfo,
    @SerializedName("name")
    val cityName: String
)
