package com.abbaskhan.features.weather

import com.abbaskhan.domain.ApiResponse
import com.abbaskhan.domain.AppError
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
    private val locationProvider: DeviceLocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        loadWeatherWithLocationCheck()
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
                            loadWeather("Berlin")
                        }
                        ApiResponse.Loading -> {}
                    }
                } else {
                    loadWeather("Berlin")
                }
            } else {
                loadWeather("Berlin")
            }
        }
    }

    fun searchCity(cityName: String) {
        if (cityName.isBlank()) return
        _uiState.value = _uiState.value.copy(cityQuery = cityName)
        loadWeather(cityName)
    }

    fun refresh() {
        loadWeatherWithLocationCheck()
    }

    private fun updateWeatherSuccess(weather: com.abbaskhan.domain.model.Weather) {
        val localDateTime = Instant
            .fromEpochMilliseconds(weather.timestamp)
            .toLocalDateTime(TimeZone.of(weather.timezone))

        val background = getWeatherBackground(
            weatherCode = weather.weatherCode,
            isDay = weather.isDay,
            localHour = localDateTime.hour
        )

        _uiState.value = _uiState.value.copy(
            weather = weather,
            background = background,
            isLoading = false,
            errorMessage = null
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
