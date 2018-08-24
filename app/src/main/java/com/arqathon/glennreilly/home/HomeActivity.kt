package com.arqathon.glennreilly.home

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.arqathon.glennreilly.augmentedaudio.R
import com.arqathon.glennreilly.augmentedaudio.R.string
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : AppCompatActivity() {

    private val adapter = HomeAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        setupToolbar()

        categories_label.setText(getString(R.string.interests_label))

        next_button.setOnClickListener{
            openNextActivity()
        }

        initialiseCategories()

    }

    private fun setupToolbar() {
        toolbar.setTitle(string.home_activity_title)
        this.setSupportActionBar(toolbar)
        supportActionBar?.let {
            
        }
    }

    private fun openNextActivity() {
        val nextIntent = Intent(this,  NextActivity::class.java)
        startActivity(nextIntent)
    }

    private fun initialiseCategories() {
        categories_list.adapter = adapter
        categories_list.layoutManager = LinearLayoutManager(this)

        adapter.clear()
        adapter.addAll(listOf(Category("history", true), Category("architecture", true)))
        adapter.initialise(listOf( "zoo", "amusement park", "library",
            "nature", "casino", "pubs"))
    }
}