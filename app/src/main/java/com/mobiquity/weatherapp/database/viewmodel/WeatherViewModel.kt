package com.mobiquity.weatherapp.database.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mobiquity.weatherapp.database.model.LocationModel
import com.mobiquity.weatherapp.database.repository.WeatherRepository

class WeatherViewModel : ViewModel() {

    var weatherData: LiveData<List<LocationModel>>? = null
    var weatherLatLonData: LiveData<LocationModel>? = null

    fun insertData(context: Context, lat: String, lon: String, name: String) {
        WeatherRepository.insertData(context, lat, lon, name)
    }

     fun getLocationDetails(context: Context): LiveData<List<LocationModel>>? {
        weatherData = WeatherRepository.getWeatherDetails(context)
        return weatherData
    }

     fun getLatLonDetails(context: Context, id: Int): LiveData<LocationModel>? {
        weatherLatLonData = WeatherRepository.getWeatherLatLonDetails(context, id)
        return weatherLatLonData
    }

     fun deleteData(context: Context): Int? {
        return WeatherRepository.deleteData(context)
    }
    fun deleteData(context: Context,id:Int): Int? {
        return WeatherRepository.deleteItemData(context,id)
    }
}