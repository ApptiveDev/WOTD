package com.apptive.wotd.model.auth

import retrofit2.http.Body
import retrofit2.http.POST

data class WeatherRequest(
    val date: String,
    val latitude: Double,
    val longitude: Double
)

data class WeatherData(
    val date: String,
    val tempFeelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val tempAvg: Double,
    val rainAmount: Double,
    val description: String
)

data class WeatherResponse(
    val isSuccess: Boolean,
    val message: String,
    val data: List<WeatherData>
)

interface WeatherApi {
    @POST("/weather/request")
    suspend fun getWeather(@Body request: WeatherRequest): WeatherResponse
}