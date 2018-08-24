package com.arqathon.glennreilly.home

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.arqathon.glennreilly.augmentedaudio.R
import com.arqathon.glennreilly.augmentedaudio.R.layout

data class Place(var name: String)

data class NextViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun setPlace(name: String) {
        view.findViewById<TextView>(R.id.place_name).text = name
    }
}

class NextAdapter(val activity: Activity) : Adapter<NextViewHolder>() {

    private val places: MutableList<Place>  = mutableListOf()

    fun addAll(items: List<Place>) {
        places.clear()
        places.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.next_list_item, parent, false)

        return NextViewHolder(view)
    }

    override fun onBindViewHolder(holder: NextViewHolder, position: Int) {
        val item = places[position]
        holder.setPlace(item.name)
    }

    override fun getItemCount(): Int {
        return places.size
    }
}