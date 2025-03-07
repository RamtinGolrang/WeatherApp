package com.weatherapp.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.weatherapp.cache.CachedWeatherData
import com.weatherapp.exception.OpenStreetMapException
import com.weatherapp.exception.WeatherApiException
import com.weatherapp.model.Coordinates
import com.weatherapp.model.OpenStreetMapResponse
import com.weatherapp.model.WeatherResponse
import com.weatherapp.model.YrResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

// Service för att hantera väderdata och API-anrop
open class WeatherService {
    private val httpClient = createHttpClient();

    private val objectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    // Enkel cache för väderdata
    private val cache = ConcurrentHashMap<String, CachedWeatherData>()
    private val cacheTTL = Duration.ofMinutes(30) // Cache-data i 30 minuter

    fun getWeatherForCity(city: String): WeatherResponse {
        // Kontrollera först om vi har cachad data som fortfarande är giltig
        val cachedData = cache[city.lowercase()]
        if (cachedData != null && !cachedData.isExpired(cacheTTL)) {
            println("Cache HIT för $city - Använder cachad data")
            return cachedData.data
        }

        println("Cache MISS för $city - Hämtar ny data från API")

        // Hämta koordinater från OpenStreetMap API
        val coordinates = getCoordinates(city)?:
            throw OpenStreetMapException("Kunde inte hitta koordinater för $city")

        // Hämta väderdata från yr.no API
        val weatherData = getWeatherFromYr(coordinates)

        // Cachea data
        cache[city.lowercase()] = CachedWeatherData(weatherData)

        return weatherData
    }

    open fun createHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private fun getCoordinates(city: String): Coordinates? {
        val encodedCity = URLEncoder.encode(city, "UTF-8")
        val request = Request.Builder()
            .url("https://nominatim.openstreetmap.org/search?format=json&q=$encodedCity")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw OpenStreetMapException("Kunde inte ansluta till OpenStreetMap API: ${response.code}")
            }

            val responseBody = response.body?.string()?:
                throw OpenStreetMapException("Fick inget svar från OpenStreetMap API")

            val locations = objectMapper.readValue<List<OpenStreetMapResponse>>(responseBody)

            if (locations.isEmpty()) {
                throw OpenStreetMapException("Inga platser hittades för '$city'")
            }

            // Kontrollerar om det är en stad eller förort. (Båda räknas som city)
            val openStreetMapResponse = locations.firstOrNull { loc ->
                loc.addresstype == "city" || loc.addresstype == "suburb"
            }

            if (openStreetMapResponse == null) {
                println("Ogiltig plats: '$city' är inte en stad. Första resultatet: ${locations.first().display_name} (Typ: ${locations.first().addresstype})")
                throw OpenStreetMapException("'$city' är inte en giltig stad. Vänligen ange en giltig stad.")
            }

            println("Hittade staden: ${openStreetMapResponse.display_name} (Typ: ${openStreetMapResponse.addresstype})")

            return Coordinates(
                latitude = openStreetMapResponse.lat.toDouble(),
                longitude = openStreetMapResponse.lon.toDouble()
            )
        }
    }

    private fun getWeatherFromYr(coordinates: Coordinates): WeatherResponse {
        try {
            val request = Request.Builder()
                .url("https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=${coordinates.latitude}&lon=${coordinates.longitude}")
                .header("User-Agent", "WeatherApp") // Behövs en User-Agent header för detta API annars går vissa anrop inte igenom.
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw WeatherApiException("Kunde inte ansluta till yr.no API: ${response.code}")
                }

                val responseBody = response.body?.string()?:
                    throw WeatherApiException("Fick inget svar från yr.no API")

                val yrResponse = objectMapper.readValue<YrResponse>(responseBody)

                // Extrahera relevant väderdata från svaret
                val weatherData = yrResponse.properties.timeseries.firstOrNull()?.data?:
                throw WeatherApiException("Ingen väderdata hittades")

                val currentWeather = weatherData.instant.details

                val weatherSymbol = weatherData.next_1_hours?.summary?.symbol_code?:
                    "unknown"

                return WeatherResponse(
                    temperature = currentWeather.air_temperature,
                    windSpeed = currentWeather.wind_speed,
                    humidity = currentWeather.relative_humidity,
                    description = getWeatherDescription(weatherSymbol),
                    symbolCode = weatherSymbol,
                    updatedAt = yrResponse.properties.meta.updated_at
                )
            }
        } catch (e: Exception) {
            if (e is WeatherApiException) throw e
            throw WeatherApiException("Fel vid hämtning av väderdata: ${e.message}")
        }
    }

    private fun getWeatherDescription(symbolCode: String): String {
        return when {
            symbolCode.contains("clearsky") -> "Klar himmel"
            symbolCode.contains("fair") -> "Mestadels klart"
            symbolCode.contains("partlycloudy") -> "Delvis molnigt"
            symbolCode.contains("cloudy") -> "Molnigt"
            symbolCode.contains("rain") -> "Regn"
            symbolCode.contains("snow") -> "Snö"
            symbolCode.contains("sleet") -> "Snöblandat regn"
            symbolCode.contains("fog") -> "Dimma"
            symbolCode.contains("thunder") -> "Åska"
            else -> "Okänt väder"
        }
    }
}