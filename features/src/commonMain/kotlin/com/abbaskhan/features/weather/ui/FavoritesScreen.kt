package com.abbaskhan.features.weather.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import atmos.features.generated.resources.*
import com.abbaskhan.domain.model.Weather
import org.jetbrains.compose.resources.painterResource

@Composable
fun FavoritesScreen(
    favoriteWeatherList: List<Weather> = emptyList(),
    onCityClick: (String) -> Unit = {},
    onRemoveClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("❤️", fontSize = 20.sp)
            Text(
                "Favorites",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }

        if (favoriteWeatherList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("❤️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Favorite Cities Yet",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explore cities from the dashboard or search and tap the heart icon to save them here for quick access.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteWeatherList) { weather ->
                    val temp = "${weather.temperatureCelsius.toInt()}°C"
                    val condition = weather.description
                    val iconRes = getWeatherIcon(weather.weatherCode, weather.isDay)

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCityClick(weather.cityName) }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Image(
                                    painter = painterResource(iconRes),
                                    contentDescription = weather.cityName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = weather.cityName,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = temp,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF334155)
                                        )
                                        Text(
                                            text = "•",
                                            fontSize = 14.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Text(
                                            text = condition,
                                            fontSize = 14.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .clickable { onRemoveClick(weather.cityName) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🗑️", fontSize = 16.sp)
                                }

                                Text(
                                    text = "›",
                                    fontSize = 24.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

internal fun getWeatherIcon(weatherCode: Int, isDay: Boolean): org.jetbrains.compose.resources.DrawableResource {
    return when (weatherCode) {
        0, 1 -> if (isDay) Res.drawable.clear_morning else Res.drawable.clear_night
        2 -> Res.drawable.partially_cloud
        3 -> Res.drawable.cloudy
        45, 48 -> Res.drawable.fog
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> if (isDay) Res.drawable.rain else Res.drawable.night_rain
        71, 73, 75, 77 -> Res.drawable.snow
        95, 96, 99 -> Res.drawable.thunder_storm
        else -> Res.drawable.clear_morning
    }
}
