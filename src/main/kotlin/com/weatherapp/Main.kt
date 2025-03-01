package com.weatherapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.weatherapp.controller.WeatherController
import com.weatherapp.service.WeatherService
import io.javalin.Javalin

fun main() {
    val weatherService = WeatherService()
    val weatherController = WeatherController(weatherService)

    val objectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)

    val app = Javalin.create { config ->
        config.http.defaultContentType = "application/json"
        config.jsonMapper(io.javalin.json.JavalinJackson(objectMapper))
        config.staticFiles.add("/public")
    }.start(7070)

    app.get("/weather", weatherController::getWeatherForCity)
    app.get("/") { ctx ->
        //ctx.result("Välkommen till väderapplikationen! Använd /weather?city=STADSNAMN för att få väderinformation.")
        ctx.redirect("/index.html")
    }

    println("Server startad på http://localhost:7070")
}
