package com.abbaskhan.features.di

import com.abbaskhan.features.weather.WeatherViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val featureModule = module {
    viewModel {
        WeatherViewModel(
            getCurrentWeather = get(),
            locationProvider = get()
        )
    }
}
