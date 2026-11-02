package com.abbaskhan.data.remote.datasource

import com.abbaskhan.data.remote.dto.CurrentWeatherResponseDto
import com.abbaskhan.data.remote.dto.GeocodingResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WeatherRemoteDataSource(
    private val client: HttpClient
) {
    suspend fun searchCity(
        cityName: String
    ): GeocodingResponseDto {
        return client.get(
            "https://geocoding-api.open-meteo.com/v1/search"
        ) {
            parameter("name", cityName)
            parameter("count", 1)
            parameter("language", "en")
            parameter("format", "json")
        }.body()
    }



    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): CurrentWeatherResponseDto =
        client.get("v1/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)

            parameter(
                "current",
                listOf(
                    "temperature_2m",
                    "relative_humidity_2m",
                    "apparent_temperature",
                    "weather_code",
                    "is_day",
                    "cloud_cover",
                    "pressure_msl",
                    "visibility",
                    "wind_speed_10m"
                ).joinToString(",")
            )

            parameter("wind_speed_unit", "ms")
            parameter("timezone", "auto")
            parameter("timeformat", "unixtime")
            parameter(
                "hourly",
                listOf(
                    "temperature_2m",
                    "apparent_temperature",
                    "relative_humidity_2m",
                    "precipitation_probability",
                    "weather_code",
                    "wind_speed_10m",
                    "is_day"
                ).joinToString(",")
            )
        }.body()

}