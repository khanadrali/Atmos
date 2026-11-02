package com.abbaskhan.features.weather.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abbaskhan.features.weather.WeatherDetailViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt
import kotlin.time.Instant

@Composable
fun WeatherDetailScreen(
    cityName: String = "Berlin",
    onBackClick: () -> Unit = {},
    viewModel: WeatherDetailViewModel = koinViewModel()
) {
    LaunchedEffect(cityName) {
        viewModel.loadWeather(cityName)
    }
    val weather by viewModel.uiState.collectAsState()
    var selectedHourlyIndex by remember { mutableStateOf(0) }

    val temp = weather?.let { "${it.temperatureCelsius.toInt()}°C" } ?: "--°C"
    val condition = when {
        weather == null -> "Loading weather..."
        else -> weather?.description ?: "Weather unavailable"
    }
    val feelsLike = weather?.let { "Feels like ${it.feelsLikeCelsius.toInt()}°C" } ?: ""
    val humidity = weather?.let { "${it.humidity}%" } ?: "--"
    val windSpeed = weather?.let { "${(it.windSpeedMetersPerSecond * 10).toInt() / 10.0} m/s" } ?: "--"
    val pressure = weather?.let { "${it.pressureHpa} hPa" } ?: "-- hPa"
    val visibility = weather?.let { "${(it.visibilityMeters / 1000.0 * 10).roundToInt() / 10.0} km" } ?: "-- km"

    val background = weather?.let {
        val localDateTime = Instant
            .fromEpochMilliseconds(it.timestamp)
            .toLocalDateTime(TimeZone.of(it.timezone))
        com.abbaskhan.features.weather.getWeatherBackground(
            weatherCode = it.weatherCode,
            isDay = it.isDay,
            localHour = localDateTime.hour
        )
    } ?: com.abbaskhan.features.weather.WeatherBackground.CLEAR_MORNING

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF144782))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        WeatherBackgroundView(
            background = background,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top App Bar with Back Button and City Name (No Favorite Button)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(onClick = onBackClick)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LocationPinIcon(
                            modifier = Modifier.size(18.dp), tint = Color.White
                        )
                        Text(
                            text = weather?.cityName ?: cityName,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(40.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Section (Temperature & Condition)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SunHeaderIcon(
                        modifier = Modifier.size(72.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = temp,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = condition,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = feelsLike,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Details Sheet with Metrics and Hourly Forecast anchored to bottom
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    topStart = 32.dp, topEnd = 32.dp
                ),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricCardPair(
                            topTitle = "Humidity", topValue = humidity, topIcon = {
                                DropletMetricIcon(modifier = Modifier.size(24.dp))
                            }, bottomTitle = "Pressure", bottomValue = pressure, bottomIcon = {
                                PressureMetricIcon(modifier = Modifier.size(24.dp))
                            }, modifier = Modifier.weight(1f)
                        )

                        MetricCardPair(
                            topTitle = "Wind", topValue = windSpeed, topIcon = {
                                WindMetricIcon(modifier = Modifier.size(24.dp))
                            }, bottomTitle = "Visibility", bottomValue = visibility, bottomIcon = {
                                VisibilityMetricIcon(modifier = Modifier.size(24.dp))
                            }, modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    HourlyForecastSection(
                        forecast = weather?.hourlyForecast.orEmpty(),
                        timezone = weather?.timezone ?: "Europe/Berlin",
                        selectedIndex = selectedHourlyIndex,
                        onSelectHourly = { index ->
                            selectedHourlyIndex = index
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}
