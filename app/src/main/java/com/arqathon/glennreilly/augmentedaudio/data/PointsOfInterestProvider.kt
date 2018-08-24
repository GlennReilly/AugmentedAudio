package com.arqathon.glennreilly.augmentedaudio.data

import android.content.Context
import com.arqathon.glennreilly.augmentedaudio.model.PointsOfInterestCollection
import com.arqathon.utils.SerializerUtil
import java.io.InputStream
import java.io.InputStreamReader


object PointsOfInterestProvider {
    const val sourceDataFileName = "locations.json"
    var pointsOfInterestCollection: PointsOfInterestCollection? = null

    fun initialise(ctx: Context) {
        val gsonBuilder = SerializerUtil.gsonBuilder
        val inputStream: InputStream = ctx?.assets.open(sourceDataFileName)
        val inputStreamReader = InputStreamReader(inputStream)
        val gson = gsonBuilder.create()
        pointsOfInterestCollection = gson.fromJson(inputStreamReader, PointsOfInterestCollection::class.java)
    }
}


