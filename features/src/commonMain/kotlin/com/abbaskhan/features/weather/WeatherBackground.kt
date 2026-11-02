package com.abbaskhan.features.weather

fun getWeatherBackground(
    weatherCode: Int,
    isDay: Boolean,
    localHour: Int
): WeatherBackground {

    return when (weatherCode) {

        0 -> when {
            !isDay -> WeatherBackground.CLEAR_NIGHT
            localHour < 11 -> WeatherBackground.CLEAR_MORNING
            localHour < 17 -> WeatherBackground.CLEAR_AFTERNOON
            else -> WeatherBackground.CLEAR_EVENING
        }

        1, 2 -> WeatherBackground.PARTLY_CLOUDY

        3 -> WeatherBackground.HEAVY_CLOUDS

        45, 48 -> WeatherBackground.FOG

        51, 53, 55, 56, 57,
        61, 63, 65, 66, 67,
        80, 81, 82 -> {
            if (isDay) WeatherBackground.RAIN
            else WeatherBackground.NIGHT_RAIN
        }

        71, 73, 75, 77, 85, 86 ->
            WeatherBackground.SNOW

        95, 96, 99 ->
            WeatherBackground.THUNDERSTORM

        else -> WeatherBackground.PARTLY_CLOUDY
    }
}


 fun getWeatherEmoji(
    weatherCode: Int,
    isDay: Boolean
): String {
    return when (weatherCode) {
        0 -> if (isDay) "☀️" else "🌙"
        1 -> if (isDay) "🌤️" else "☁️"
        2 -> "⛅"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55, 56, 57 -> "🌦️"
        61, 63, 65, 66, 67 -> "🌧️"
        71, 73, 75, 77 -> "❄️"
        80, 81, 82 -> "🌧️"
        85, 86 -> "🌨️"
        95, 96, 99 -> "⛈️"
        else -> "🌤️"
    }
}


enum class WeatherBackground {
    CLEAR_MORNING,
    CLEAR_AFTERNOON,
    CLEAR_EVENING,
    CLEAR_NIGHT,
    CLOUDY,
    HEAVY_CLOUDS,
    RAIN,
    NIGHT_RAIN,
    THUNDERSTORM,
    SNOW,
    FOG,
    PARTLY_CLOUDY
}