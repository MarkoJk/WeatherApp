package com.test.weather.data.entities

data class WindInfo(
    val speed: Float,
    val deg: Int
) {
    fun speedInKmH(): Float = speed * 3.6f
}
