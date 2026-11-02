package com.abbaskhan.atmos

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.abbaskhan.features.weather.ui.WeatherDashboardScreen

@Composable
fun App() {
    MaterialTheme {
//        WeatherScreen()
        WeatherDashboardScreen()
    }
}