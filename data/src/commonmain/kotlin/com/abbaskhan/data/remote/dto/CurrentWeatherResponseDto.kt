package com.abbaskhan.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CurrentWeatherResponseDto(
    val current: CurrentWeatherDto,
    val hourly: HourlyWeatherDto,
    val timezone: String
)

@Serializable
data class CurrentWeatherDto(
    val time: Long,

    @SerialName("temperature_2m")
    val temperature: Double,

    @SerialName("relative_humidity_2m")
    val humidity: Int,

    @SerialName("apparent_temperature")
    val apparentTemperature: Double,

    @SerialName("weather_code")
    val weatherCode: Int,

    @SerialName("is_day")
    val isDay: Int,

    @SerialName("cloud_cover")
    val cloudCover: Int,

    @SerialName("pressure_msl")
    val pressure: Double,

    val visibility: Double,

    @SerialName("wind_speed_10m")
    val windSpeed: Double
)