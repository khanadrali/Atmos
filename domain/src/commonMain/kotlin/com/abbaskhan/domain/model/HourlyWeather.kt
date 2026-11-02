package com.abbaskhan.domain.model


data class HourlyWeather(
    val timestamp: Long,
    val temperatureCelsius: Double,
    val weatherCode: Int,
    val isDay: Boolean
)