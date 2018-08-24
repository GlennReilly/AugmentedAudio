package com.arqathon.glennreilly.home

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.arqathon.glennreilly.augmentedaudio.R

data class Category(val name: String, val checked: Boolean = false)

data class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun setCategory(name: String) {
        view.findViewById<TextView>(R.id.category_text).text = name
    }

    fun setChecked(checked: Boolean) {
        view.findViewById<CheckBox>(R.id.category_checkbox).isChecked = checked
    }
}

class HomeAdapter(val activity: Activity) : RecyclerView.Adapter<ViewHolder>() {

    private val categories: MutableList<Category>  = mutableListOf()

    fun clear() {
        categories.clear()
    }

    fun addAll(items: List<Category>) {
        categories.addAll(items)
    }

    fun initialise(items: List<String>) {
        for (item in items) {
            categories.add(Category(item))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categories[position]
        holder.setCategory(item.name)
        holder.setChecked(item.checked)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}