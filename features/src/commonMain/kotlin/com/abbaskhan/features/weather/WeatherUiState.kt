package com.abbaskhan.features.weather

import com.abbaskhan.domain.model.Weather

data class WeatherUiState(
    val cityQuery: String = "Berlin",
    val weather: Weather? = null,
    val background: WeatherBackground =
        WeatherBackground.CLEAR_MORNING,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val isFavorite: Boolean = false,
    val favoriteWeatherList: List<Weather> = emptyList(),
    val recentSearches: List<String> = emptyList()
)
