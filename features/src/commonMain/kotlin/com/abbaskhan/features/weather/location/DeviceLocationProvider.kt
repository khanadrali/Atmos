package com.abbaskhan.features.weather.location

import com.abbaskhan.features.model.DeviceLocation

interface DeviceLocationProvider {
    suspend fun hasLocationPermission(): Boolean
    suspend fun requestLocationPermission(): Boolean
    suspend fun getCurrentLocation(): DeviceLocation?
}
