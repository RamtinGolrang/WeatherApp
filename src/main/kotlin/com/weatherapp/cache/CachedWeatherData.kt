package com.weatherapp.cache

import com.weatherapp.model.WeatherResponse
import java.time.Duration

// Cache-modell
data class CachedWeatherData(
    val data: WeatherResponse,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun isExpired(ttl: Duration): Boolean {
        val now = System.currentTimeMillis()
        return now - timestamp > ttl.toMillis()
    }
}