package com.mobiquity.weatherapp.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.mobiquity.weatherapp.database.model.LocationModel
import com.mobiquity.weatherapp.database.room.WeatherDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class WeatherRepository {

    companion object {

        var weatherDatabase: WeatherDatabase? = null

        var locationModel: LiveData<List<LocationModel>>? = null
        var latlonModel: LiveData<LocationModel>? = null
        var delete: Int = 0

        fun initializeDB(context: Context): WeatherDatabase {
            return WeatherDatabase.getDatabaseClient(context)
        }

        fun insertData(context: Context, lat: String, lon: String, name: String) {

            weatherDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val loginDetails = LocationModel(lat, lon, name)
                weatherDatabase!!.weatherDao().insertData(loginDetails)
            }

        }

         fun getWeatherDetails(context: Context): LiveData<List<LocationModel>>? {

            weatherDatabase = initializeDB(context)
            CoroutineScope(IO).launch {
                locationModel = weatherDatabase!!.weatherDao().getLocationDetails()
            }
            return locationModel
        }


         fun getWeatherLatLonDetails(context: Context, id: Int): LiveData<LocationModel>? {

            weatherDatabase = initializeDB(context)
            CoroutineScope(IO).launch {
                latlonModel = weatherDatabase!!.weatherDao().getLatLonDetails(id)
            }
            return latlonModel
        }

         fun deleteData(context: Context): Int? {
            weatherDatabase = initializeDB(context)
            CoroutineScope(IO).launch {
                delete = weatherDatabase!!.weatherDao().deleteData()
            }
            return delete
        }

        fun deleteItemData(context: Context,id:Int): Int? {
            weatherDatabase = initializeDB(context)
            CoroutineScope(IO).launch {
                delete = weatherDatabase!!.weatherDao().deleteItem(id)
            }
            return delete
        }

    }
}