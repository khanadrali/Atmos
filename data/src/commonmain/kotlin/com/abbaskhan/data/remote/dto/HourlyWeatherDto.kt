package com.abbaskhan.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HourlyWeatherDto(
    val time: List<Long>,
    @SerialName("temperature_2m")
    val temperature: List<Double>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("is_day")
    val isDay: List<Int>
)