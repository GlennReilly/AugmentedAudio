package com.arqathon.glennreilly.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.arqathon.glennreilly.augmentedaudio.R
import com.arqathon.glennreilly.augmentedaudio.R.drawable
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.next_activity.*

class NextActivity : AppCompatActivity() {

    private val adapter = NextAdapter(this)

    private var buttonPlay = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.next_activity)

        setupToolbar()

        initPalyButton(!buttonPlay)

        play_button.setOnClickListener{
            initPalyButton(buttonPlay)
        }

        initialiseCategories()

    }

    private fun setupToolbar() {
        next_toolbar.setTitle(R.string.next_activity_title)
        this.setSupportActionBar(next_toolbar)
        supportActionBar?.let {
             it.setDisplayHomeAsUpEnabled(true)
             it.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        }
    }

    private fun initPalyButton(play: Boolean) {
        if (!play) {
            play_button.setImageResource(drawable.ic_pause_circle_outline_black_24dp)
            buttonPlay = true
        } else {
            play_button.setImageResource(drawable.ic_play_circle_outline_black_24dp)
            buttonPlay = false
        }
    }

    private fun initialiseCategories() {
        places_list.adapter = adapter
        places_list.layoutManager = LinearLayoutManager(this)

        adapter.addAll(listOf(Place("Town Hall"), Place("The Rocks")))
    }
}