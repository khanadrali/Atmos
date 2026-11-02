package com.abbaskhan.domain.usecase


import com.abbaskhan.domain.ApiResponse
import com.abbaskhan.domain.model.Weather
import com.abbaskhan.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(
        cityName: String
    ): ApiResponse<Weather> {
        return weatherRepository.getCurrentWeather(cityName)
    }

    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        cityName: String = "Current Location"
    ): ApiResponse<Weather> {
        return weatherRepository.getCurrentWeather(latitude, longitude, cityName)
    }
}
