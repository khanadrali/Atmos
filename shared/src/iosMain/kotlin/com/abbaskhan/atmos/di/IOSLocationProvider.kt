package com.abbaskhan.atmos.di

import com.abbaskhan.features.model.DeviceLocation
import com.abbaskhan.features.weather.location.DeviceLocationProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.dsl.module
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class IOSLocationProvider : DeviceLocationProvider {
    private val delegate = LocationDelegate()
    private val locationManager = CLLocationManager().apply {
        delegate = this@IOSLocationProvider.delegate
    }

    override suspend fun hasLocationPermission(): Boolean {
        val status = locationManager.authorizationStatus
        return status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusAuthorizedAlways
    }

    override suspend fun requestLocationPermission(): Boolean {
        val status = locationManager.authorizationStatus
        if (status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusAuthorizedAlways) {
            return true
        }
        if (status == kCLAuthorizationStatusDenied || status == kCLAuthorizationStatusRestricted) {
            return false
        }
        return suspendCancellableCoroutine { continuation ->
            delegate.permissionContinuation = continuation
            locationManager.requestWhenInUseAuthorization()
        }
    }

    override suspend fun getCurrentLocation(): DeviceLocation? {
        if (!hasLocationPermission()) {
            val granted = requestLocationPermission()
            if (!granted) return null
        }

        return suspendCancellableCoroutine { continuation ->
            delegate.locationContinuation = continuation
            locationManager.requestLocation()
        }
    }
}

class LocationDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    var permissionContinuation: Continuation<Boolean>? = null
    var locationContinuation: Continuation<DeviceLocation?>? = null

    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: Int) {
        val granted = didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedAlways
        if (didChangeAuthorizationStatus != kCLAuthorizationStatusNotDetermined) {
            permissionContinuation?.resume(granted)
            permissionContinuation = null
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation
        if (location != null) {
            location.coordinate.useContents {
                locationContinuation?.resume(DeviceLocation(latitude, longitude))
            }
        } else {
            locationContinuation?.resume(null)
        }
        locationContinuation = null
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        locationContinuation?.resume(null)
        locationContinuation = null
    }
}

val iosLocationModule = module {
    single<DeviceLocationProvider> { IOSLocationProvider() }
}
