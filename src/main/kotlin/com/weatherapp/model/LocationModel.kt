package com.weatherapp.model

// Datamodeller för OpenStreetMap
data class Coordinates(val latitude: Double, val longitude: Double)

data class OpenStreetMapResponse(
    val lat: String,
    val lon: String,
    val display_name: String,
    val addresstype: String
)