package com.mobiquity.weatherapp.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mobiquity.weatherapp.database.model.LocationModel


@Database(entities = [LocationModel::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao() : DAOAccess

    companion object {

        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabaseClient(context: Context) : WeatherDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, WeatherDatabase::class.java, "Weather_db")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}