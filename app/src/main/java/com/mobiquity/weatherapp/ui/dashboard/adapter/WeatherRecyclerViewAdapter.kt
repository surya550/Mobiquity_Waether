package com.mobiquity.weatherapp.ui.dashboard.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.mobiquity.weatherapp.R
import com.mobiquity.weatherapp.database.model.LocationModel
import kotlinx.android.synthetic.main.custom_view.view.*
import java.util.*
import kotlin.collections.ArrayList


class WeatherRecyclerViewAdapter(
    var list: List<LocationModel>,
    private val userClickCallbacks: UserClickCallbacks
) :
    RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherViewHolder>(), Filterable {

    var locationFilterList = ArrayList<LocationModel>()

    init {
        locationFilterList = list as ArrayList<LocationModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_view, parent, false)
        return WeatherViewHolder(view)
    }

    override fun getItemCount(): Int {
        return locationFilterList.size
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {


        val currentPosition = locationFilterList[position]
        holder.itemView.cityName.text = currentPosition.name

    }

    // Inner class for viewHolder
    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener,View.OnLongClickListener {
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        override fun onClick(v: View?) {
            userClickCallbacks.onUserClick(locationFilterList[adapterPosition])
        }

        override fun onLongClick(v: View?): Boolean {
            userClickCallbacks.onUserLongClick(locationFilterList[adapterPosition])
            return true
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    locationFilterList = list as ArrayList<LocationModel>
                } else {
                    val resultList = ArrayList<LocationModel>()
                    for (row in list) {
                        if (row.name.lowercase().contains(constraint.toString().lowercase())) {
                            resultList.add(row)
                        }
                    }
                    locationFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = locationFilterList
                return filterResults

            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                locationFilterList = results?.values as ArrayList<LocationModel>
                notifyDataSetChanged()
            }

        }
    }
}
