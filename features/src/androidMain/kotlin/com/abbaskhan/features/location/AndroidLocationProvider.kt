package com.abbaskhan.features.location

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.abbaskhan.features.model.DeviceLocation
import com.abbaskhan.features.weather.location.DeviceLocationProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

class AndroidLocationProvider(private val androidContext: Context) : DeviceLocationProvider {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(androidContext)

    override suspend fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            androidContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            androidContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    override suspend fun requestLocationPermission(): Boolean {
        return hasLocationPermission()
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): DeviceLocation? {
        if (!hasLocationPermission()) return null

        return withTimeoutOrNull(3.seconds) {
            suspendCancellableCoroutine { continuation ->
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            continuation.resume(DeviceLocation(location.latitude, location.longitude))
                        } else {
                            fusedLocationClient.getCurrentLocation(
                                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                                null
                            ).addOnSuccessListener { freshLocation: Location? ->
                                if (freshLocation != null) {
                                    continuation.resume(DeviceLocation(freshLocation.latitude, freshLocation.longitude))
                                } else {
                                    continuation.resume(null)
                                }
                            }.addOnFailureListener {
                                continuation.resume(null)
                            }
                        }
                    }.addOnFailureListener {
                        continuation.resume(null)
                    }
                } catch (_: SecurityException) {
                    continuation.resume(null)
                }
            }
        }
    }
}
