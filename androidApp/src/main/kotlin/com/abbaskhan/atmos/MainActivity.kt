package com.abbaskhan.atmos

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.abbaskhan.atmos.di.initKoin
import com.abbaskhan.features.weather.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModel()
    private lateinit var locationPermissionResultLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initKoin(this)

        locationPermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                          permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            if (granted) {
                viewModel.onPermissionGranted()
            } else {
                viewModel.loadDefaultWeather()
            }
        }

        setContent {
            App()
        }

        locationPermissionResultLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
