package com.abbaskhan.domain.repository

import com.abbaskhan.domain.ApiResponse
import com.abbaskhan.domain.model.Weather

interface WeatherRepository {

    suspend fun getCurrentWeather(
        cityName: String
    ): ApiResponse<Weather>

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        cityName: String = "Current Location"
    ): ApiResponse<Weather>
}
