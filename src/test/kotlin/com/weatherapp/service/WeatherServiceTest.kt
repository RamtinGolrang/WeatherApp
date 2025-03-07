package com.weatherapp.service

import com.weatherapp.exception.OpenStreetMapException
import com.weatherapp.exception.WeatherApiException
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Enhetstester för WeatherService
 */
class WeatherServiceTest {

    private lateinit var weatherService: WeatherService
    private lateinit var mockHttpClient: OkHttpClient
    private lateinit var mockCall: Call
    private lateinit var mockResponse: Response
    private lateinit var mockResponseBody: ResponseBody

    @BeforeEach
    fun setUp() {
        // Skapa mock-objekt för alla externa beroenden
        mockHttpClient = Mockito.mock(OkHttpClient::class.java)
        mockCall = Mockito.mock(Call::class.java)
        mockResponse = Mockito.mock(Response::class.java)
        mockResponseBody = Mockito.mock(ResponseBody::class.java)

        // Sätt upp grundläggande mock-beteende
        whenever(mockHttpClient.newCall(any())).thenReturn(mockCall)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockResponse.body).thenReturn(mockResponseBody)
        whenever(mockResponse.isSuccessful).thenReturn(true)

        // Skapa en WeatherService med mockad HttpClient
        weatherService = object : WeatherService() {
            override fun createHttpClient(): OkHttpClient {
                return mockHttpClient
            }
        }
    }

    @Test
    fun `getWeatherForCity should return weather data when API calls are successful`() {
        // Arrange - Konfigurera mock-svar för OpenStreetMap
        val locationJson = """[{"lat":"59.3293","lon":"18.0686","display_name":"Stockholm, Sverige","addresstype":"city"}]"""
        val weatherJson = """
            {
                "properties": {
                    "meta": {
                        "updated_at": "2023-10-16T12:00:00Z"
                    },
                    "timeseries": [
                        {
                            "time": "2023-10-16T12:00:00Z",
                            "data": {
                                "instant": {
                                    "details": {
                                        "air_temperature": 15.2,
                                        "wind_speed": 3.5,
                                        "relative_humidity": 65.0
                                    }
                                },
                                "next_1_hours": {
                                    "summary": {
                                        "symbol_code": "partlycloudy_day"
                                    }
                                }
                            }
                        }
                    ]
                }
            }
        """

        // Konfigurera mock att svara med location och därefter yr.no svar
        whenever(mockResponseBody.string())
            .thenReturn(locationJson)  // Första anropet för OpenStreetMap
            .thenReturn(weatherJson)   // Andra anropet för yr.no

        // Act - Anropa metoden som ska testas
        val result = weatherService.getWeatherForCity("Stockholm")

        // Assert - Verifiera resultatet
        assertEquals(15.2, result.temperature)
        assertEquals(3.5, result.windSpeed)
        assertEquals(65.0, result.humidity)
        assertEquals("Delvis molnigt", result.description)
        assertEquals("partlycloudy_day", result.symbolCode)
        assertEquals("2023-10-16T12:00:00Z", result.updatedAt)

        // Verifiera att HTTP-anrop gjordes två gånger (en för varje API)
        verify(mockHttpClient, times(2)).newCall(any())
    }

    @Test
    fun `getWeatherForCity should throw LocationNotFoundException when no locations found`() {
        // Arrange - Simulera ett tomt svar från OpenStreetMap
        whenever(mockResponseBody.string()).thenReturn("[]")

        // Act & Assert - Verifiera att rätt undantag kastas
        val exception = assertThrows<OpenStreetMapException> {
            weatherService.getWeatherForCity("NonExistentCity")
        }

        // Verifiera felmeddelandet
        assertTrue(exception.message!!.contains("Inga platser hittades"))
    }

    @Test
    fun `getWeatherForCity should throw LocationNotFoundException when input is not a city`() {
        // Arrange - Simulera ett svar med en plats som inte är en stad
        val nonCityJson = """[{"lat":"59.3293","lon":"18.0686","display_name":"Stockholm Bridge, Sverige","addresstype":"bridge"}]"""
        whenever(mockResponseBody.string()).thenReturn(nonCityJson)

        // Act & Assert - Verifiera att rätt undantag kastas
        val exception = assertThrows<OpenStreetMapException> {
            weatherService.getWeatherForCity("Stockholm Bridge")
        }

        // Verifiera felmeddelandet
        assertTrue(exception.message!!.contains("inte en giltig stad"))
    }

    @Test
    fun `getWeatherForCity should return cached data when available and not expired`() {
        // Arrange - Konfigurera mock-svar för första anropet
        val locationJson = """[{"lat":"59.3293","lon":"18.0686","display_name":"Stockholm, Sverige","addresstype":"city"}]"""
        val weatherJson = """
            {
                "properties": {
                    "meta": {
                        "updated_at": "2023-10-16T12:00:00Z"
                    },
                    "timeseries": [
                        {
                            "time": "2023-10-16T12:00:00Z",
                            "data": {
                                "instant": {
                                    "details": {
                                        "air_temperature": 15.2,
                                        "wind_speed": 3.5,
                                        "relative_humidity": 65.0
                                    }
                                },
                                "next_1_hours": {
                                    "summary": {
                                        "symbol_code": "partlycloudy_day"
                                    }
                                }
                            }
                        }
                    ]
                }
            }
        """

        // Konfigurera mock att svara med location och därefter yr.no svar
        whenever(mockResponseBody.string())
            .thenReturn(locationJson)
            .thenReturn(weatherJson)

        // Act - Anropa metoden två gånger för samma stad
        val result1 = weatherService.getWeatherForCity("Stockholm")
        val result2 = weatherService.getWeatherForCity("Stockholm")

        // Assert - Verifiera att båda resultaten är samma
        assertEquals(result1, result2)

        // Verifiera att HTTP-anrop bara gjordes två gånger (för första anropet) och inte fyra gånger (vilket skulle vara fallet utan caching)
        verify(mockHttpClient, times(2)).newCall(any())
    }

    @Test
    fun `getWeatherForCity should throw WeatherDataException when yr API fails`() {
        // Arrange - Simulera framgångsrikt svar från OpenStreetMap men misslyckat från yr.no
        val locationJson = """[{"lat":"59.3293","lon":"18.0686","display_name":"Stockholm, Sverige","addresstype":"city"}]"""

        // Konfigurera mockResponseBody för första anropet (OpenStreetMap)
        whenever(mockResponseBody.string())
            .thenReturn(locationJson)
            .thenThrow(RuntimeException("API error"))

        // Act & Assert - Verifiera att rätt undantag kastas
        assertThrows<WeatherApiException> {
            weatherService.getWeatherForCity("Stockholm")
        }
    }
}