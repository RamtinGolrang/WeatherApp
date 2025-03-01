package com.weatherapp.controller

import com.weatherapp.exception.OpenStreetMapException
import com.weatherapp.exception.WeatherApiException
import com.weatherapp.service.WeatherService
import io.javalin.http.Context

// Controller som hanterar HTTP-förfrågningar
class WeatherController(private val weatherService: WeatherService) {
    fun getWeatherForCity(ctx: Context) {
        val city = ctx.queryParam("city")

        if (city.isNullOrBlank()) {
            ctx.status(400).json(mapOf("error" to "Staden måste anges som en 'city' parameter"))
            return
        }

        try {
            val weatherData = weatherService.getWeatherForCity(city)
            ctx.json(weatherData)
        } catch (e: OpenStreetMapException) {
            ctx.status(404).json(mapOf("error" to "Kunde inte hitta plats: ${e.message}"))
        } catch (e: OpenStreetMapException) {
            ctx.status(400).json(mapOf("error" to "Ogiltig platstyp: ${e.message}"))
        } catch (e: WeatherApiException) {
            ctx.status(500).json(mapOf("error" to "Kunde inte hämta väderdata: ${e.message}"))
        } catch (e: Exception) {
            ctx.status(500).json(mapOf("error" to "Ett internt fel inträffade: ${e.message}"))
        }
    }
}