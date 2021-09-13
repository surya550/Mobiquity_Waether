package com.mobiquity.weatherapp.database.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationDetails(val id: Int?, val lat: String, val lon: String, val name: String) : Parcelable
