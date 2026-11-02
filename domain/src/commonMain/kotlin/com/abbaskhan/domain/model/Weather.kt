package com.abbaskhan.domain.model


data class Weather(
    val cityName: String,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val humidity: Int,
    val windSpeedMetersPerSecond: Double,
    val pressureHpa: Double,
    val visibilityMeters: Double,
    val description: String,
    val iconCode: String,
    val weatherCode: Int,
    val isDay: Boolean,
    val cloudCover: Int,
    val timestamp: Long,
    val timezone: String,
    val hourlyForecast: List<HourlyWeather>
)