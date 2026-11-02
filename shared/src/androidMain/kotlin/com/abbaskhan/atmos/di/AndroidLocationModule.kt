package com.abbaskhan.atmos.di

import com.abbaskhan.features.location.AndroidLocationProvider
import com.abbaskhan.features.weather.location.DeviceLocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val androidLocationModule = module {
    single<DeviceLocationProvider> {
        AndroidLocationProvider(androidContext())
    }
}
