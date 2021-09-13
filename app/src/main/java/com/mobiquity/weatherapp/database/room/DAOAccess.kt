package com.mobiquity.weatherapp.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mobiquity.weatherapp.database.model.LocationModel

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(locationModel: LocationModel)

    @Query("SELECT * FROM weatherDetails ORDER BY id DESC")
     fun getLocationDetails(): LiveData<List<LocationModel>>

    @Query("SELECT * FROM weatherDetails WHERE id = :id  ORDER BY id DESC")
     fun getLatLonDetails(id: Int): LiveData<LocationModel>
/*
    @Query("SELECT * FROM weatherDetails WHERE name LIKE '%' || :value || '%' ORDER BY id DESC")
     fun getSearchDetails(value: String?): LiveData<List<LocationModel>>*/

    @Query("DELETE FROM weatherDetails ")
     fun deleteData(): Int

    @Query("DELETE FROM weatherDetails WHERE id = :id")
    fun deleteItem(id:Int): Int
}