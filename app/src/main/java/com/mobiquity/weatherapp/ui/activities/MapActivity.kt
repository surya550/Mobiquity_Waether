package com.mobiquity.weatherapp.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mobiquity.surya.utils.LatLngInterpolator.Spherical
import com.mobiquity.surya.utils.MarkerAnimation.animateMarkerToGB
import com.mobiquity.weatherapp.MainActivity
import com.mobiquity.weatherapp.R
import com.mobiquity.weatherapp.database.viewmodel.WeatherViewModel
import java.io.IOException
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocationMarker: Marker? = null
    private lateinit var currentLocation: Location
    private var firstTimeFlag = true
    private var supportMapFragment: SupportMapFragment? = null
    private var _address: TextView? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var geocoder: Geocoder? = null
    private var address: String? = null
    private var addresses: List<Address>? = null
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            currentLocation = locationResult.lastLocation
            if (firstTimeFlag && googleMap != null) {
                animateCamera(currentLocation)
                firstTimeFlag = false
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude
                showMarker(currentLocation.latitude, currentLocation.longitude)
            }
        }
    }

    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)


        supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)
        findViewById<View>(R.id.currentLocationImageButton).setOnClickListener { v ->
            if (v.id == R.id.currentLocationImageButton && googleMap != null) animateCamera(
                currentLocation
            )
        }
        _address = findViewById(R.id.address)
        findViewById<View>(R.id.btn_add).setOnClickListener { sharecurrentLocation() }
    }

    private fun startCurrentLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2000
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this@MapActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@MapActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MapActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
                return
            }
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val isGooglePlayServicesAvailable: Boolean
        get() {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val status =
                googleApiAvailability.isGooglePlayServicesAvailable(this@MapActivity)
            if (ConnectionResult.SUCCESS == status) return true else {
                if (googleApiAvailability.isUserResolvableError(status)) Toast.makeText(
                    this@MapActivity,
                    "Please Install google play services to use this application",
                    Toast.LENGTH_LONG
                ).show()
            }
            return false
        }

    private fun animateCamera(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap!!.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                getCameraPositionWithBearing(
                    latLng
                )
            )
        )
    }

    private fun getCameraPositionWithBearing(latLng: LatLng): CameraPosition {
        return CameraPosition.Builder().target(latLng).zoom(16f).build()
    }

    private fun showMarker(latitude: Double, longitude: Double) {
        try {
            val latLng = LatLng(latitude, longitude)
            if (currentLocationMarker == null) currentLocationMarker = googleMap!!.addMarker(
                MarkerOptions().title("My Location").icon(BitmapDescriptorFactory.defaultMarker())
                    .position(latLng)
            ) else animateMarkerToGB(
                currentLocationMarker!!, latLng, Spherical()
            )
            geocoder = Geocoder(this@MapActivity, Locale.getDefault())
            try {
                addresses = geocoder!!.getFromLocation(
                    latitude,
                    longitude,
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                address = (addresses as MutableList<Address>?)?.get(0)
                    ?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                /*city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();*/
            } catch (e: IOException) {
                e.printStackTrace()
            }
            _address!!.text = address
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
        if (isGooglePlayServicesAvailable) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            startCurrentLocationUpdates()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (fusedLocationProviderClient != null) fusedLocationProviderClient!!.removeLocationUpdates(
            mLocationCallback
        )
    }

    public override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient = null
        googleMap = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        supportMapFragment!!.onResume()
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            latitude = latLng.latitude
            longitude = latLng.longitude
            googleMap.addMarker(
                MarkerOptions().title("My Location").icon(BitmapDescriptorFactory.defaultMarker())
                    .position(latLng)
            )
            showMarker(latLng.latitude, latLng.longitude)
        }
    }

    private fun sharecurrentLocation() {
        geocoder = Geocoder(this@MapActivity, Locale.getDefault())
        try {
            addresses = geocoder!!.getFromLocation(
                latitude,
                longitude,
                1
            )
            address = (addresses as MutableList<Address>?)?.get(0)
                ?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            var city = (addresses as MutableList<Address>?)?.get(0)?.locality;
            val state = (addresses as MutableList<Address>?)?.get(0)?.adminArea;
            val country = (addresses as MutableList<Address>?)?.get(0)?.countryName

            if (city.isNullOrEmpty())
                city = "Unknown"
            weatherViewModel.insertData(
                this, latitude.toString(), longitude.toString(),
                city.toString() + " , " + state.toString()
            )

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445
    }

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this@MapActivity, MainActivity::class.java))
        finish()
    }
}
