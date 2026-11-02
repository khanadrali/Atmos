package com.abbaskhan.features.weather.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abbaskhan.domain.model.HourlyWeather
import com.abbaskhan.features.model.HourlyItemData
import com.abbaskhan.features.weather.getWeatherEmoji
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Instant


@Composable
fun HourlyForecastSection(
    forecast: List<HourlyWeather>,
    timezone: String,
    selectedIndex: Int,
    onSelectHourly: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hourlyItems = remember(forecast, timezone) {
        forecast.take(24).mapIndexed { index, weather ->
            val localDateTime = Instant
                .fromEpochMilliseconds(weather.timestamp)
                .toLocalDateTime(TimeZone.of(timezone))

            HourlyItemData(
                time = if (index == 0) {
                    "Now"
                } else {
                    "${localDateTime.hour.toString().padStart(2, '0')}:00"
                },
                temp = "${weather.temperatureCelsius.roundToInt()}°",
                weatherCode = weather.weatherCode,
                isDay = weather.isDay
            )
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hourly forecast",
                color = Color(0xFF14243B),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Next 24 hours",
                color = Color(0xFF8B9BB0),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (hourlyItems.isEmpty()) {
            Text(
                text = "Hourly forecast unavailable",
                color = Color(0xFF8B9BB0),
                fontSize = 14.sp
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(hourlyItems) { index, item ->
                    HourlyForecastItem(
                        item = item,
                        isSelected = selectedIndex == index,
                        onClick = { onSelectHourly(index) }
                    )
                }
            }
        }
    }
}


@Composable
 fun HourlyForecastItem(
    item: HourlyItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color(0xFFECF3FE)
    } else {
        Color(0xFFF7FAFE)
    }

    val borderColor = if (isSelected) {
        Color(0xFFD4E3FC)
    } else {
        Color.Transparent
    }

    val textColor = if (isSelected) {
        Color(0xFF1A60B7)
    } else {
        Color(0xFF8B9BB0)
    }

    val tempColor = if (isSelected) {
        Color(0xFF1A60B7)
    } else {
        Color(0xFF14243B)
    }

    Column(
        modifier = Modifier
            .width(68.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = item.time,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            }
        )

        Text(
            text = getWeatherEmoji(item.weatherCode, item.isDay),
            fontSize = 25.sp
        )

        Text(
            text = item.temp,
            color = tempColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
