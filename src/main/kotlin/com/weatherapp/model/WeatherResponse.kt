package com.weatherapp.model

data class WeatherResponse(
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Double,
    val description: String,
    val symbolCode: String,
    val updatedAt: String
)