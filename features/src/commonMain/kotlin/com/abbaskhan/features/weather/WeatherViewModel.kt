package com.abbaskhan.features.weather

import com.abbaskhan.features.shouldAutoLoadLocationOnStart
import com.abbaskhan.domain.ApiResponse
import com.abbaskhan.domain.AppError
import com.abbaskhan.domain.repository.FavoritesRepository
import com.abbaskhan.domain.usecase.GetCurrentWeatherUseCase
import com.abbaskhan.features.weather.location.DeviceLocationProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class WeatherViewModel(
    private val getCurrentWeather: GetCurrentWeatherUseCase,
    private val locationProvider: DeviceLocationProvider,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteWeather()
        loadRecentSearches()
        if (shouldAutoLoadLocationOnStart) {
            loadWeatherWithLocationCheck()
        }
    }

    fun loadDefaultWeather() {
        loadWeather()
        loadFavoriteWeather()
        loadRecentSearches()
    }

    fun onPermissionGranted() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                when (val result = getCurrentWeather(location.latitude, location.longitude, "Current Location")) {
                    is ApiResponse.Success -> updateWeatherSuccess(result.data)
                    else -> loadWeather()
                }
            } else {
                loadWeather()
            }
            loadFavoriteWeather()
            loadRecentSearches()
        }
    }

    fun loadWeatherWithLocationCheck() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            var hasPermission = locationProvider.hasLocationPermission()
            if (!hasPermission) {
                hasPermission = locationProvider.requestLocationPermission()
            }
            if (hasPermission) {
                val location = locationProvider.getCurrentLocation()
                if (location != null) {
                    when (val result = getCurrentWeather(location.latitude, location.longitude, "Current Location")) {
                        is ApiResponse.Success -> {
                            updateWeatherSuccess(result.data)
                        }
                        is ApiResponse.Error -> {
                            loadWeather()
                        }
                        ApiResponse.Loading -> {}
                    }
                } else {
                    loadWeather()
                }
            } else {
                loadWeather()
            }
            loadFavoriteWeather()
            loadRecentSearches()
        }
    }

    fun searchCity(cityName: String) {
        if (cityName.isBlank()) return
        recordSearch(cityName)
        _uiState.value = _uiState.value.copy(cityQuery = cityName)
        loadWeather(cityName)
    }

    fun refresh() {
        loadWeatherWithLocationCheck()
        loadFavoriteWeather()
        loadRecentSearches()
    }

    fun loadRecentSearches() {
        viewModelScope.launch {
            val searches = favoritesRepository.getRecentSearches()
            _uiState.value = _uiState.value.copy(recentSearches = searches)
        }
    }

    fun recordSearch(cityName: String) {
        if (cityName.isBlank()) return
        viewModelScope.launch {
            favoritesRepository.addRecentSearch(cityName.trim())
            loadRecentSearches()
        }
    }

    private fun resolveCityName(weather: com.abbaskhan.domain.model.Weather): String {
        if (weather.cityName == "Current Location" || weather.cityName.isBlank()) {
            val parts = weather.timezone.split("/")
            if (parts.size > 1) {
                return parts.last().replace('_', ' ')
            }
        }
        return weather.cityName
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val weather = _uiState.value.weather ?: return@launch
            val cityName = resolveCityName(weather)
            if (cityName.isBlank()) return@launch
            val isFav = favoritesRepository.isFavorite(cityName)
            if (isFav) {
                favoritesRepository.removeFavorite(cityName)
            } else {
                favoritesRepository.addFavorite(cityName)
            }
            _uiState.value = _uiState.value.copy(isFavorite = !isFav)
            loadFavoriteWeather()
        }
    }

    fun loadFavoriteWeather() {
        viewModelScope.launch {
            val cities = favoritesRepository.getFavoriteCities()
            val weatherList = mutableListOf<com.abbaskhan.domain.model.Weather>()
            for (city in cities) {
                when (val result = getCurrentWeather(city)) {
                    is ApiResponse.Success -> weatherList.add(result.data)
                    else -> {}
                }
            }
            _uiState.value = _uiState.value.copy(favoriteWeatherList = weatherList)
        }
    }

    fun removeFavorite(cityName: String) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(cityName)
            loadFavoriteWeather()
            val currentWeather = _uiState.value.weather
            if (currentWeather != null && resolveCityName(currentWeather) == cityName) {
                _uiState.value = _uiState.value.copy(isFavorite = false)
            }
        }
    }

    private suspend fun updateWeatherSuccess(weather: com.abbaskhan.domain.model.Weather) {
        val localDateTime = Instant
            .fromEpochMilliseconds(weather.timestamp)
            .toLocalDateTime(TimeZone.of(weather.timezone))

        val background = getWeatherBackground(
            weatherCode = weather.weatherCode,
            isDay = weather.isDay,
            localHour = localDateTime.hour
        )

        val resolvedName = resolveCityName(weather)
        val isFav = favoritesRepository.isFavorite(resolvedName)

        _uiState.value = _uiState.value.copy(
            weather = weather,
            background = background,
            isLoading = false,
            errorMessage = null,
            isFavorite = isFav
        )
    }

    private fun loadWeather(cityName: String = "Berlin") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                cityQuery = cityName
            )

            when (val result = getCurrentWeather(cityName)) {
                is ApiResponse.Success -> {
                    updateWeatherSuccess(result.data)
                }

                is ApiResponse.Error -> {
                    val message = when (result.message) {
                        AppError.Network -> "Network error. Check your connection."
                        AppError.NotFound -> "City not found."
                        AppError.Unknown -> "Something went wrong."
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }

                ApiResponse.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
            }
        }
    }
}
