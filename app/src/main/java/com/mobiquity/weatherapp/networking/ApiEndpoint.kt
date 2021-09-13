package com.mobiquity.weatherapp.networking

import android.content.Context
import android.content.SharedPreferences
import com.mobiquity.weatherapp.ui.settings.SettingsFragment

object ApiEndpoint {

    var BASEURL = "http://api.openweathermap.org/data/2.5/"
    var CurrentWeather = "weather?"
    var ListWeather = "forecast?"
    var Daily = "forecast/daily?"
    var onecall = "onecall?"
    var UnitsAppid =  "&cnt=5&appid=0e4685921ce18fe23ca70b475bb147e5"
    var UnitsAppidDaily =
         "&cnt=1&appid=0e4685921ce18fe23ca70b475bb147e5"


}