package com.weatherapp.model

data class YrResponse(
    val properties: YrProperties
)

data class YrProperties(
    val meta: YrMeta,
    val timeseries: List<YrTimeseries>
)

data class YrMeta(
    val updated_at: String
)

data class YrTimeseries(
    val time: String,
    val data: YrData
)

data class YrData(
    val instant: YrInstant,
    val next_1_hours: YrNextHours? = null
)

data class YrInstant(
    val details: YrDetails
)

data class YrDetails(
    val air_temperature: Double,
    val wind_speed: Double,
    val relative_humidity: Double
)

data class YrNextHours(
    val summary: YrSummary
)

data class YrSummary(
    val symbol_code: String
)