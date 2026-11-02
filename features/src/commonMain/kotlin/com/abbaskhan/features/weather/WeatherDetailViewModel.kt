package com.abbaskhan.features.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abbaskhan.domain.ApiResponse
import com.abbaskhan.domain.model.Weather
import com.abbaskhan.domain.usecase.GetCurrentWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherDetailViewModel(
    private val getCurrentWeather: GetCurrentWeatherUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<Weather?>(null)
    val uiState: StateFlow<Weather?> = _uiState.asStateFlow()

    fun loadWeather(cityName: String) {
        viewModelScope.launch {
            _uiState.value = null
            when (val result = getCurrentWeather(cityName)) {
                is ApiResponse.Success -> {
                    _uiState.value = result.data
                }
                else -> {}
            }
        }
    }
}
