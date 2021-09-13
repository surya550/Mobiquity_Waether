package com.mobiquity.weatherapp.ui.dashboard.adapter

import com.mobiquity.weatherapp.database.model.LocationModel

interface UserClickCallbacks {
    fun onUserClick(locationModel: LocationModel)
    fun onUserLongClick(locationModel: LocationModel)
}
