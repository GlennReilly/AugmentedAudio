package com.arqathon.glennreilly.augmentedaudio.model


data class PointsOfInterestCollection(val pointsOfInterestCollection: List<PointOfInterest>)

data class PointOfInterest(
    val lat: Float,
    val lon: Float,
    val proximity: Int,
    val type: String,
    val name: String,
    val headline: String,
    val detail: String,
    val categories: List<String>
)
