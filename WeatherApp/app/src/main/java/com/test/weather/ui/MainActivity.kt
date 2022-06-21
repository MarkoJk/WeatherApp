package com.test.weather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.weather.R
import com.test.weather.data.WeatherDataSource
import com.test.weather.data.entities.Weather
import com.test.weather.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var weatherDataSource: WeatherDataSource

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result: Boolean ->
            if (result)
                getMyPosition()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getWeatherInMyPosition()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        if (menu != null) {
            val item: MenuItem = menu.findItem(R.id.action_search)
            val searchView: SearchView = item.actionView as SearchView
            searchView.queryHint = getString(R.string.search_hint)

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    getWeatherByCityName(query)

                    searchView.isIconified = true
                    searchView.onActionViewCollapsed()

                    return false
                }
            })
        }

        return true
    }

    private fun getWeatherInMyPosition() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getMyPosition()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                showLocationPermissionRationale()
            }
            else -> {
                locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
    }

    private fun updateUI(weather: Weather?, city: String) {
        var currentCity = ""
        var currentTemp = ""
        var currentTempDesc = getString(R.string.temp_not_founded, city)
        var iconURL = ""
        var feelsTemp = ""
        var humidity = ""
        var wind = ""

        if (weather != null) {
            currentCity = weather.cityName
            currentTemp = getString(
                R.string.temp_representation_in_celsius,
                weather.tempInfo.temp.roundToInt()
            )
            feelsTemp = getString(
                R.string.feels_temp_representation_in_celsius,
                weather.tempInfo.feels_like.roundToInt()
            )
            humidity = getString(
                R.string.humidity_representation_in_percent,
                weather.tempInfo.humidity
            )
            wind = getString(
                R.string.wind_representation_in_kmh,
                weather.windInfo.speedInKmH()
            )

            if (!weather.info.isNullOrEmpty()) {
                currentTempDesc = weather.info[0].main
                iconURL = "https://openweathermap.org/img/wn/" + weather.info[0].icon + "@4x.png"
            }
        }

        binding.city.text = currentCity
        binding.city.isVisible = currentCity.isNotEmpty()

        binding.currentTemp.text = currentTemp
        binding.currentTempDesc.text = currentTempDesc
        binding.feelsTemp.text = feelsTemp

        binding.humidityValue.text = humidity
        binding.humidityContainer.isVisible = humidity.isNotEmpty()
        binding.windValue.text = wind
        binding.windContainer.isVisible = wind.isNotEmpty()

        if (iconURL.isNotEmpty()) {
            Glide
                .with(this)
                .load(iconURL)
                .into(binding.imgCurrentTempIcon)
        } else
            binding.imgCurrentTempIcon.setImageBitmap(null)
    }

    @SuppressLint("MissingPermission")
    private fun getMyPosition() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getWeatherByLocation(location)
                }
            }
    }

    private fun getWeatherByLocation(location: Location) {
        weatherDataSource.getWeather(location.latitude, location.longitude) { weather: Weather? ->
            updateUI(weather, "${location.latitude} x ${location.longitude} position")
        }
    }

    private fun getWeatherByCityName(cityName: String) {
        weatherDataSource.getWeather(cityName) { weather: Weather? ->
            updateUI(weather, cityName)
        }
    }

    private fun showLocationPermissionRationale() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.location_rationale_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
