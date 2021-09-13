package com.mobiquity.weatherapp.ui.activities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobiquity.weatherapp.R
import com.mobiquity.weatherapp.model.ModelNextDay
import com.mobiquity.weatherapp.networking.ApiEndpoint
import com.mobiquity.weatherapp.ui.adapter.NextDayAdapter
import kotlinx.android.synthetic.main.activity_city.*
import kotlinx.android.synthetic.main.fragment_next_day.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FragmentNextDays : BottomSheetDialogFragment() {

    var lat: Double? = 17.3850
    var lng: Double? = 78.4867
    var nextDayAdapter: NextDayAdapter? = null
    var rvListWeather: ShimmerRecyclerView? = null
    var fabClose: FloatingActionButton? = null
    var modelNextDays: MutableList<ModelNextDay> = ArrayList()

    private val sharedPrefFile = "unit_preference"

    var unit: String? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_next_day, container, false)
         val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        unit = sharedPreferences.getString("unit", "")


        nextDayAdapter = NextDayAdapter(requireActivity(), modelNextDays)
        rvListWeather = rootView.rvListWeather
        rvListWeather?.layoutManager = LinearLayoutManager(activity)
        rvListWeather?.setHasFixedSize(true)
        rvListWeather?.adapter = nextDayAdapter
        rvListWeather?.showShimmerAdapter()

        fabClose = rootView.findViewById(R.id.fabClose)
        fabClose?.setOnClickListener {
            modelNextDays.clear()
            rvListWeather?.adapter?.notifyDataSetChanged()

            dismiss()
        }


        Handler(getMainLooper()).postDelayed({
            getListWeather()
        }, 2000)
        return rootView
    }


    private fun getListWeather() {

        AndroidNetworking.get(ApiEndpoint.BASEURL + ApiEndpoint.onecall + "lat=" + lat + "&lon=" + lng + "&units="+unit + ApiEndpoint.UnitsAppidDaily)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val jsonArray = response.getJSONArray("daily")

                        for (i in 0 until jsonArray.length()) {
                            val dataApi = ModelNextDay()
                            val objectList = jsonArray.getJSONObject(i)
                            val jsonObjectOne = objectList.getJSONObject("temp")
                            val jsonArrayOne = objectList.getJSONArray("weather")
                            val jsonObjectTwo = jsonArrayOne.getJSONObject(0)
                            val longDate = objectList.optLong("dt")
                            val formatDate = SimpleDateFormat("d MMM yy")
                            val readableDate = formatDate.format(Date(longDate * 1000))
                            val longDay = objectList.optLong("dt")
                            val format = SimpleDateFormat("EEEE")
                            val readableDay = format.format(Date(longDay * 1000))

                            dataApi.nameDate = readableDate
                            dataApi.nameDay = readableDay
                            dataApi.descWeather = jsonObjectTwo.getString("description")
                            dataApi.tempMin = jsonObjectOne.getDouble("min")
                            dataApi.tempMax = jsonObjectOne.getDouble("max")


                            modelNextDays.add(dataApi)
                        }
                        nextDayAdapter?.notifyDataSetChanged()
                        rvListWeather?.hideShimmerAdapter()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            requireContext(),
                            "Failed to display header data!",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(
                        requireContext(),
                        "No internet network!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    companion object {
        fun newInstance(string: String?): FragmentNextDays {
            val fragmentNextDays = FragmentNextDays()
            val args = Bundle()
            args.putString("string", string)
            fragmentNextDays.arguments = args
            return fragmentNextDays
        }
    }
}