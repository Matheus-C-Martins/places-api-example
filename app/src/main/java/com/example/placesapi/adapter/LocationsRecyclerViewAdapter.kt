package com.example.placesapi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.placesapi.R

class LocationsRecyclerViewAdapter(context: Context?, data: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mData: List<String> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val view = mInflater.inflate(R.layout.location_label, parent, false)
                LabelViewHolder(view)
            } else -> {
                val view = mInflater.inflate(R.layout.location_item, parent, false)
                LocationViewHolder(view)
            }
        }
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
            } else -> {
                val viewHolder = holder as LocationViewHolder
                val values = mData[position].split(",")
                viewHolder.locationName.text = values[0]
                viewHolder.locationType.text = values[1]
            }
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class LabelViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var locationLabel: TextView = itemView.findViewById(R.id.location_label)
    }

    inner class LocationViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var locationName: TextView = itemView.findViewById(R.id.location_name)
        var locationType: TextView = itemView.findViewById(R.id.location_type)
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): String {
        return mData[id]
    }
}