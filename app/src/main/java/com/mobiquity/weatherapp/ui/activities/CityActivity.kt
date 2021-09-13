package com.mobiquity.weatherapp.ui.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.mobiquity.weatherapp.MainActivity
import com.mobiquity.weatherapp.R
import com.mobiquity.weatherapp.database.model.LocationDetails
import com.mobiquity.weatherapp.model.ModelMain
import com.mobiquity.weatherapp.networking.ApiEndpoint
import com.mobiquity.weatherapp.ui.adapter.MainAdapter
import kotlinx.android.synthetic.main.activity_city.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@DelicateCoroutinesApi
class CityActivity : AppCompatActivity() {

    private var today: String? = null
    private var mainAdapter: MainAdapter? = null
    private val modelMain: MutableList<ModelMain> = ArrayList()
    private var locationDetails: LocationDetails? = null

    private val sharedPrefFile = "unit_preference"
    private var format: String? = null

    var unit: String? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        val sharedPreferences: SharedPreferences = this@CityActivity.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        locationDetails = intent.getParcelableExtra("locationDetails")

        val dateNow = Calendar.getInstance().time
        today = DateFormat.format("EEE", dateNow) as String

        unit = sharedPreferences.getString("unit", "")
        format = if (unit.equals("metric")) {
            "%.0f°C"
        } else
            "%.0f°f"

        val fragmentNextDays = FragmentNextDays.newInstance("FragmentNextDays")
        mainAdapter = MainAdapter(this, modelMain)

        rvListWeather.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvListWeather.setHasFixedSize(true)
        rvListWeather.adapter = mainAdapter

        fabNextDays.setOnClickListener {
            fragmentNextDays.show(supportFragmentManager, fragmentNextDays.tag)
        }

        //method get LatLong & get Date
        getToday()

        GlobalScope.launch {
            getCurrentWeather()
            getListWeather()
        }


    }

    private fun getToday() {
        val date = Calendar.getInstance().time
        val dateFormat = DateFormat.format("d MMM yyyy", date) as String
        val formatDate = "$today, $dateFormat"
        tvDate.text = formatDate
    }


    private fun getCurrentWeather() {

        AndroidNetworking.get(ApiEndpoint.BASEURL + ApiEndpoint.CurrentWeather + "lat=" + locationDetails!!.lat + "&lon=" + locationDetails!!.lon + "&units=" + unit + ApiEndpoint.UnitsAppid)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                @SuppressLint("SetTextI18n")
                override fun onResponse(response: JSONObject) {

                    try {
                        val jsonArrayOne = response.getJSONArray("weather")
                        val jsonObjectOne = jsonArrayOne.getJSONObject(0)
                        val jsonObjectTwo = response.getJSONObject("main")
                        val jsonObjectThree = response.getJSONObject("wind")
                        val strWeather = jsonObjectOne.getString("main")
                        val strDescWeather = jsonObjectOne.getString("description")
                        val strWindVelocity = jsonObjectThree.getString("speed")
                        val strHumidity = jsonObjectTwo.getString("humidity")
                        val strLocation = response.getString("name")
                        val dblTemperature = jsonObjectTwo.getDouble("temp")

                        when (strDescWeather) {
                            "broken clouds" -> {
                                iconTemp.setAnimation(R.raw.broken_clouds)
                                tvWeather.text = "Scattered Clouds"
                            }
                            "light rain" -> {
                                iconTemp.setAnimation(R.raw.light_rain)
                                tvWeather.text = "Drizzling"
                            }
                            "haze" -> {
                                iconTemp.setAnimation(R.raw.broken_clouds)
                                tvWeather.text = "Foggy"
                            }
                            "overcast clouds" -> {
                                iconTemp.setAnimation(R.raw.overcast_clouds)
                                tvWeather.text = "Cloudy"
                            }
                            "moderate rain" -> {
                                iconTemp.setAnimation(R.raw.moderate_rain)
                                tvWeather.text = "Light rain"
                            }
                            "few clouds" -> {
                                iconTemp.setAnimation(R.raw.few_clouds)
                                tvWeather.text = "Cloudy"
                            }
                            "heavy intensity rain" -> {
                                iconTemp.setAnimation(R.raw.heavy_intentsity)
                                tvWeather.text = "heavy rain"
                            }
                            "clear sky" -> {
                                iconTemp.setAnimation(R.raw.clear_sky)
                                tvWeather.text = "clear sky"
                            }
                            "scattered clouds" -> {
                                iconTemp.setAnimation(R.raw.scattered_clouds)
                                tvWeather.text = "Scattered Clouds"
                            }
                            else -> {
                                iconTemp.setAnimation(R.raw.unknown)
                                tvWeather.text = strWeather
                            }
                        }

                        tvLocation.text = locationDetails!!.name
                        tvTemperature.text =
                            String.format(Locale.getDefault(), format!!, dblTemperature)
                        tvWindVelocity.text = "Wind velocity $strWindVelocity km/h"
                        tvHumidity.text = "Humidity $strHumidity %"
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@CityActivity,
                            "Failed to display header data!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(
                        this@CityActivity,
                        "No internet network!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })
    }

    private fun getListWeather() {

        AndroidNetworking.get(ApiEndpoint.BASEURL + ApiEndpoint.ListWeather + "lat=" + locationDetails!!.lat + "&lon=" + locationDetails!!.lon + "&units=" + unit + ApiEndpoint.UnitsAppid)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                @SuppressLint("SimpleDateFormat")
                override fun onResponse(response: JSONObject) {
                    try {
                        val jsonArray = response.getJSONArray("list")
                        for (i in 0 until jsonArray.length()) {
                            val dataApi = ModelMain()
                            val objectList = jsonArray.getJSONObject(i)
                            val jsonObjectOne = objectList.getJSONObject("main")
                            val jsonArrayOne = objectList.getJSONArray("weather")
                            val jsonObjectTwo = jsonArrayOne.getJSONObject(0)
                            var timeNow = objectList.getString("dt_txt")
                            val formatDefault = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val formatTimeCustom = SimpleDateFormat("kk:mm")

                            try {
                                val timesFormat = formatDefault.parse(timeNow)
                                timeNow = formatTimeCustom.format(timesFormat)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }

                            dataApi.timeNow = timeNow
                            dataApi.currentTemp = jsonObjectOne.getDouble("temp")
                            dataApi.descWeather = jsonObjectTwo.getString("description")
                            dataApi.tempMin = jsonObjectOne.getDouble("temp_min")
                            dataApi.tempMax = jsonObjectOne.getDouble("temp_max")
                            modelMain.add(dataApi)
                        }
                        mainAdapter?.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@CityActivity,
                            "Failed to display header data!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(
                        this@CityActivity,
                        "No internet network!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CityActivity, MainActivity::class.java))

    }

}