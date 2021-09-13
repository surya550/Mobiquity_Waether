package com.mobiquity.weatherapp.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobiquity.weatherapp.database.model.LocationDetails
import com.mobiquity.weatherapp.database.model.LocationModel
import com.mobiquity.weatherapp.database.viewmodel.WeatherViewModel
import com.mobiquity.weatherapp.databinding.FragmentDashboardBinding
import com.mobiquity.weatherapp.ui.activities.CityActivity
import com.mobiquity.weatherapp.ui.activities.MapActivity
import com.mobiquity.weatherapp.ui.dashboard.adapter.UserClickCallbacks
import com.mobiquity.weatherapp.ui.dashboard.adapter.WeatherRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.*
import java.lang.Runnable


class DashboardFragment : Fragment(), UserClickCallbacks {

    private lateinit var weatherViewModel: WeatherViewModel
    private var _binding: FragmentDashboardBinding? = null

    lateinit var list: LiveData<LocationModel>
    var weatherRecyclerViewAdapter: WeatherRecyclerViewAdapter? = null
    private val binding get() = _binding!!
    private lateinit var locationDetails: LocationDetails
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loadData(this)

        val addCity: FloatingActionButton = binding.addCity
        val search: SearchView = binding.searchView
        val tvHeader: TextView = binding.tvHeader
        tvHeader.text = "Dashboard"
        addCity.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }


        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                weatherRecyclerViewAdapter?.filter?.filter(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

        })

        return root
    }

    override fun onResume() {
        super.onResume()
        loadData(this)
    }

    private fun loadData(userClickCallbacks: UserClickCallbacks) {

            weatherViewModel.getLocationDetails(requireContext())?.observe(requireActivity(), {

                if (it.isEmpty()) {
                    no_data.visibility = View.VISIBLE
                    no_data.text = "Data Not Found!!!"
                } else {
                    no_data.visibility = View.GONE
                    weatherRecyclerViewAdapter = WeatherRecyclerViewAdapter(it, userClickCallbacks)
                    recyclerView?.adapter = weatherRecyclerViewAdapter
                }

            })

    }


    override fun onUserClick(locationModel: LocationModel) {


        locationDetails = LocationDetails(
            locationModel.Id,
            locationModel.lat,
            locationModel.lon,
            locationModel.name
        )

        startActivity(
            Intent(requireContext(), CityActivity::class.java)
                .putExtra("locationDetails", locationDetails)
        )
        requireActivity().finish()


        //}
    }

    override fun onUserLongClick(locationModel: LocationModel) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Are you sure? ")
        builder.setMessage("you want to delete " + locationModel.name + " from database?")

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->

            CoroutineScope(Dispatchers.IO).launch {
                locationModel.Id?.let { weatherViewModel.deleteData(requireContext(), it) }

                toast(requireContext(), "you have successfully deleted the location")
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }


        builder.show()
    }


    private fun toast(context: Context?, text: String?) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable { Toast.makeText(context, text, Toast.LENGTH_LONG).show() })

    }


}