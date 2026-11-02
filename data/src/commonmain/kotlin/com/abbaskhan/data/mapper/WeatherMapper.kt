package com.abbaskhan.data.mapper


import com.abbaskhan.data.remote.dto.CurrentWeatherResponseDto
import com.abbaskhan.data.remote.dto.HourlyWeatherDto
import com.abbaskhan.domain.model.HourlyWeather
import com.abbaskhan.domain.model.Weather

fun CurrentWeatherResponseDto.toDomain(
    cityName: String
): Weather {
    val weather = current

    return Weather(
        cityName = cityName,
        temperatureCelsius = weather.temperature,
        feelsLikeCelsius = weather.apparentTemperature,
        humidity = weather.humidity,
        windSpeedMetersPerSecond = weather.windSpeed,
        pressureHpa = weather.pressure,
        visibilityMeters = weather.visibility,
        description = weather.weatherCode.toWeatherDescription(),
        iconCode = weather.weatherCode.toString(),
        weatherCode = weather.weatherCode,
        isDay = weather.isDay == 1,
        cloudCover = weather.cloudCover,
        timestamp = weather.time * 1000L,
        timezone = timezone,
        hourlyForecast = hourly.toDomain()
    )
}

fun HourlyWeatherDto.toDomain(): List<HourlyWeather> {
    return time.indices.map { index ->
        HourlyWeather(
            timestamp = time[index] * 1000L,
            temperatureCelsius = temperature[index],
            weatherCode = weatherCode[index],
            isDay = isDay[index] == 1
        )
    }
}