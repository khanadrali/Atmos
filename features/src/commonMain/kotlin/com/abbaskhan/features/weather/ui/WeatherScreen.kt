package com.abbaskhan.features.weather.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import atmos.features.generated.resources.*
import com.abbaskhan.domain.model.HourlyWeather
import com.abbaskhan.features.weather.WeatherBackground
import com.abbaskhan.features.weather.WeatherViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun WeatherDashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = koinViewModel(),
    onHomeNavClick: () -> Unit = {},
    onSearchNavClick: () -> Unit = {},
    onFavoritesNavClick: () -> Unit = {},
) {


    val state by viewModel.uiState.collectAsState()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = {
            viewModel.refresh()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        // Your existing dashboard content


        val state by viewModel.uiState.collectAsState()
        val weather = state.weather


        var selectedBottomTab by remember { mutableStateOf(0) }
        var selectedHourlyIndex by remember { mutableStateOf(0) }
        var showSearchDialog by remember { mutableStateOf(false) }
        var searchQuery by remember(state.cityQuery) {
            mutableStateOf(state.cityQuery)
        }

        val cityName = weather?.cityName ?: state.cityQuery

        val temperature = weather?.let {
            "${it.temperatureCelsius.toInt()}°C"
        } ?: "--°C"

        val condition = when {
            state.isLoading && weather == null -> "Loading weather..."
            state.errorMessage != null && weather == null -> "Weather unavailable"
            else -> weather?.description ?: "Weather unavailable"
        }

        val feelsLike = weather?.let {
            "Feels like ${it.feelsLikeCelsius.toInt()}°C"
        } ?: ""

        val humidity = weather?.let {
            "${it.humidity}%"
        } ?: "--"

        val windSpeed = weather?.let {
            "${(it.windSpeedMetersPerSecond * 10).toInt() / 10.0} m/s"
        } ?: "--"

        val pressure = weather?.let {
            "${it.pressureHpa} hPa"
        } ?: "-- hPa"
        val visibility = weather?.let {
            "${(it.visibilityMeters / 1000.0 * 10).roundToInt() / 10.0} km"
        } ?: "-- km"

        LaunchedEffect(weather?.cityName) {
            selectedHourlyIndex = 0
        }


        Box(
            modifier = modifier.fillMaxSize().background(Color(0xFF144782))
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    when (selectedBottomTab) {
                        0 -> {
                            WeatherBackgroundView(
                                background = state.background, modifier = Modifier.fillMaxSize()
                            )

                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                TopNavigationBar(
                                    cityName = cityName,
                                    onLocationClick = {
                                        showSearchDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 12.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                WeatherHeroSection(
                                    temperature = temperature,
                                    condition = condition,
                                    feelsLike = feelsLike,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                                )

                                if (state.isLoading && weather == null) {
                                    ShowProgress()
                                }

                                state.errorMessage?.let { error ->
                                    Text(
                                        text = error,
                                        color = Color(0xFFFFD6D6),
                                        fontSize = 13.sp,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                            .padding(horizontal = 24.dp, vertical = 8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(120.dp))

                                WeatherDetailsSheet(
                                    humidity = humidity,
                                    windSpeed = windSpeed,
                                    pressure = pressure,
                                    visibility = visibility,
                                    hourlyForecasts = weather?.hourlyForecast.orEmpty(),
                                    timezone = weather?.timezone ?: "Europe/Berlin",
                                    selectedIndex = selectedHourlyIndex,
                                    onSelectHourly = { index ->
                                        selectedHourlyIndex = index
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        1 -> {
                            SearchCityScreen(
                                onBackClick = { selectedBottomTab = 0 },
                                onCitySelected = { city ->
                                    viewModel.searchCity(city)
                                    selectedBottomTab = 0
                                })
                        }

                        2 -> {
                            FavoritesScreen(
                                onCityClick = { city ->
                                    viewModel.searchCity(city)
                                    selectedBottomTab = 0
                                })
                        }
                    }
                }

                BottomNavigationBar(
                    selectedIndex = selectedBottomTab, onTabSelected = { index ->
                        selectedBottomTab = index
                        when (index) {
                            0 -> onHomeNavClick()
                            1 -> onSearchNavClick()
                            2 -> onFavoritesNavClick()
                        }
                    }, modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (showSearchDialog) {
            AlertDialog(onDismissRequest = {
                showSearchDialog = false
            }, title = {
                Text("Search city")
            }, text = {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    label = { Text("City name") },
                    placeholder = { Text("e.g. Berlin") })
            }, confirmButton = {
                TextButton(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchCity(searchQuery.trim())
                            showSearchDialog = false
                            selectedBottomTab = 0
                        }
                    }) {
                    Text("Search")
                }
            }, dismissButton = {
                TextButton(
                    onClick = {
                        showSearchDialog = false
                    }) {
                    Text("Cancel")
                }
            })
        }
    }
}

@Composable
private fun TopNavigationBar(
    cityName: String,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)).clickable(onClick = onLocationClick)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LocationPinIcon(
                modifier = Modifier.size(18.dp), tint = Color.White
            )

            Text(
                text = cityName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold
            )

        }

    }
}

@Composable
private fun WeatherHeroSection(
    temperature: String, condition: String, feelsLike: String, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SunHeaderIcon(
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = temperature,
            color = Color.White,
            fontSize = 68.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = condition,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = feelsLike,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun WeatherDetailsSheet(
    humidity: String,
    windSpeed: String,
    pressure: String,
    visibility: String,
    hourlyForecasts: List<HourlyWeather>,
    timezone: String,
    selectedIndex: Int,
    onSelectHourly: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier, shape = RoundedCornerShape(
            topStart = 32.dp, topEnd = 32.dp
        ), color = Color.White, shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCardPair(
                    topTitle = "Humidity", topValue = humidity, topIcon = {
                        DropletMetricIcon(
                            modifier = Modifier.size(24.dp)
                        )
                    }, bottomTitle = "Pressure", bottomValue = pressure, bottomIcon = {
                        PressureMetricIcon(
                            modifier = Modifier.size(24.dp)
                        )
                    }, modifier = Modifier.weight(1f)
                )

                MetricCardPair(
                    topTitle = "Wind", topValue = windSpeed, topIcon = {
                        WindMetricIcon(
                            modifier = Modifier.size(24.dp)
                        )
                    }, bottomTitle = "Visibility", bottomValue = visibility, bottomIcon = {
                        VisibilityMetricIcon(
                            modifier = Modifier.size(24.dp)
                        )
                    }, modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            HourlyForecastSection(
                forecast = hourlyForecasts,
                timezone = timezone,
                selectedIndex = selectedIndex,
                onSelectHourly = onSelectHourly,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
        }
    }
}


@Composable
private fun MetricCardPair(
    topTitle: String,
    topValue: String,
    topIcon: @Composable () -> Unit,
    bottomTitle: String,
    bottomValue: String,
    bottomIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            color = Color(0xFFF4F8FE), shape = RoundedCornerShape(20.dp)
        ).border(
            width = 1.dp, color = Color(0xFFE5EEFA), shape = RoundedCornerShape(20.dp)
        ).padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            MetricRow(
                title = topTitle, value = topValue, icon = topIcon
            )

            Spacer(modifier = Modifier.height(12.dp))

            Canvas(
                modifier = Modifier.fillMaxWidth().height(1.dp)
            ) {
                drawLine(
                    color = Color(0xFFE2ECF8),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MetricRow(
                title = bottomTitle, value = bottomValue, icon = bottomIcon
            )
        }
    }
}

@Composable
private fun MetricRow(
    title: String, value: String, icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Column {
            Text(
                text = title,
                color = Color(0xFF7B8C9E),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                color = Color(0xFF14243B),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//@Composable
//private fun HourlyForecastItem(
//    item: HourlyItemData,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    val backgroundColor = if (isSelected) Color(0xFFECF3FE) else Color(0xFFF7FAFE)
//    val borderColor = if (isSelected) Color(0xFFD4E3FC) else Color.Transparent
//    val textColor = if (isSelected) Color(0xFF1A60B7) else Color(0xFF8B9BB0)
//    val tempColor = if (isSelected) Color(0xFF1A60B7) else Color(0xFF14243B)
//
//    Column(
//        modifier = Modifier
//            .width(68.dp)
//            .clip(RoundedCornerShape(18.dp))
//            .background(backgroundColor)
//            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
//            .clickable(onClick = onClick)
//            .padding(vertical = 14.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        Text(
//            text = item.time,
//            color = textColor,
//            fontSize = 13.sp,
//            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
//        )
//
//        SunSmallIcon(
//            modifier = Modifier.size(24.dp)
//        )
//
//        Text(
//            text = item.temp,
//            color = tempColor,
//            fontSize = 15.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}

@Composable
private fun BottomNavigationBar(
    selectedIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier, color = Color.White, shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Home",
                isSelected = selectedIndex == 0,
                icon = { tint -> HomeNavIcon(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )

            BottomNavItem(
                label = "Search",
                isSelected = selectedIndex == 1,
                icon = { tint -> SearchNavIcon(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )

            BottomNavItem(
                label = "Favorites",
                isSelected = selectedIndex == 2,
                icon = { tint -> HeartNavIcon(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { onTabSelected(2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    isSelected: Boolean,
    icon: @Composable (Color) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (isSelected) Color(0xFF1A60B7) else Color(0xFF8B9BB0)

    Column(
        modifier = modifier.fillMaxHeight().clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon(tint)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun HeroBackground(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF133663), Color(0xFF1C5393), Color(0xFF327CC4), Color(0xFF539ADF)
                )
            ), size = size
        )

        val skylineTop = height * 0.58f
        val horizonY = height * 0.78f

        val skylinePath = Path().apply {
            moveTo(0f, horizonY)
            lineTo(0f, skylineTop + 60f)
            lineTo(width * 0.05f, skylineTop + 60f)
            lineTo(width * 0.05f, skylineTop + 40f)
            lineTo(width * 0.08f, skylineTop + 40f)
            lineTo(width * 0.08f, skylineTop + 70f)
            lineTo(width * 0.12f, skylineTop + 70f)
            lineTo(width * 0.12f, skylineTop + 20f)
            lineTo(width * 0.16f, skylineTop + 20f)
            lineTo(width * 0.16f, skylineTop + 60f)
            lineTo(width * 0.22f, skylineTop + 60f)
            lineTo(width * 0.22f, skylineTop + 35f)
            lineTo(width * 0.26f, skylineTop + 35f)
            lineTo(width * 0.26f, skylineTop + 75f)
            lineTo(width * 0.32f, skylineTop + 75f)

            lineTo(width * 0.32f, skylineTop + 45f)
            lineTo(width * 0.36f, skylineTop + 45f)
            lineTo(width * 0.36f, skylineTop + 80f)
            lineTo(width * 0.40f, skylineTop + 80f)

            lineTo(width * 0.40f, skylineTop + 15f)
            lineTo(width * 0.43f, skylineTop + 15f)
            lineTo(width * 0.43f, skylineTop + 65f)
            lineTo(width * 0.48f, skylineTop + 65f)

            lineTo(width * 0.48f, skylineTop + 50f)
            lineTo(width * 0.52f, skylineTop + 50f)
            lineTo(width * 0.52f, skylineTop + 85f)
            lineTo(width * 0.58f, skylineTop + 85f)

            lineTo(width * 0.58f, skylineTop - 30f)
            lineTo(width * 0.595f, skylineTop - 30f)
            lineTo(width * 0.595f, skylineTop - 65f)
            lineTo(width * 0.605f, skylineTop - 65f)
            lineTo(width * 0.605f, skylineTop - 30f)
            lineTo(width * 0.62f, skylineTop - 30f)
            lineTo(width * 0.62f, skylineTop + 40f)
            lineTo(width * 0.66f, skylineTop + 40f)

            lineTo(width * 0.66f, skylineTop + 70f)
            lineTo(width * 0.72f, skylineTop + 70f)
            lineTo(width * 0.72f, skylineTop + 25f)
            lineTo(width * 0.77f, skylineTop + 25f)

            lineTo(width * 0.77f, skylineTop - 15f)
            lineTo(width * 0.785f, skylineTop - 15f)
            lineTo(width * 0.785f, skylineTop + 25f)
            lineTo(width * 0.82f, skylineTop + 25f)
            lineTo(width * 0.82f, skylineTop + 60f)

            lineTo(width * 0.88f, skylineTop + 60f)
            lineTo(width * 0.88f, skylineTop + 35f)
            lineTo(width * 0.93f, skylineTop + 35f)
            lineTo(width * 0.93f, skylineTop + 80f)
            lineTo(width, skylineTop + 80f)
            lineTo(width, horizonY)
            close()
        }

        drawPath(
            path = skylinePath, brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF887669), Color(0xFF5A585C), Color(0xFF2C3E55)
                ), startY = skylineTop - 65f, endY = horizonY
            )
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2B4D72), Color(0xFF1D3C5E), Color(0xFF162E48)
                )
            ), topLeft = Offset(0f, horizonY), size = Size(width, height - horizonY)
        )

        val waterReflectionPath = Path().apply {
            moveTo(0f, horizonY)
            lineTo(width, horizonY)
            lineTo(width, horizonY + 40f)
            lineTo(0f, horizonY + 40f)
            close()
        }
        drawPath(
            path = waterReflectionPath, brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0x66B09880), Color(0x001D3C5E)
                )
            )
        )
    }
}

// Icon Canvas Composables

@Composable
private fun SunHeaderIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.26f
        val rayLength = size.minDimension * 0.12f
        val rayOffset = size.minDimension * 0.35f

        drawCircle(
            color = Color(0xFFFFCC00), radius = radius, center = center
        )

        for (i in 0 until 8) {
            val angle = (i * 45.0) * (kotlin.math.PI / 180.0)
            val startX = center.x + (rayOffset * kotlin.math.cos(angle)).toFloat()
            val startY = center.y + (rayOffset * kotlin.math.sin(angle)).toFloat()
            val endX = center.x + ((rayOffset + rayLength) * kotlin.math.cos(angle)).toFloat()
            val endY = center.y + ((rayOffset + rayLength) * kotlin.math.sin(angle)).toFloat()

            drawLine(
                color = Color(0xFFFFCC00),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun SunSmallIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.25f
        val rayLength = size.minDimension * 0.10f
        val rayOffset = size.minDimension * 0.34f

        drawCircle(
            color = Color(0xFFFFC107), radius = radius, center = center
        )

        for (i in 0 until 8) {
            val angle = (i * 45.0) * (kotlin.math.PI / 180.0)
            val startX = center.x + (rayOffset * kotlin.math.cos(angle)).toFloat()
            val startY = center.y + (rayOffset * kotlin.math.sin(angle)).toFloat()
            val endX = center.x + ((rayOffset + rayLength) * kotlin.math.cos(angle)).toFloat()
            val endY = center.y + ((rayOffset + rayLength) * kotlin.math.sin(angle)).toFloat()

            drawLine(
                color = Color(0xFFFFC107),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun MenuIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.5.dp.toPx()

        drawLine(
            color = tint,
            start = Offset(0f, h * 0.25f),
            end = Offset(w, h * 0.25f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = Offset(0f, h * 0.5f),
            end = Offset(w * 0.75f, h * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = tint,
            start = Offset(0f, h * 0.75f),
            end = Offset(w, h * 0.75f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun LocationPinIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.95f)
            cubicTo(w * 0.2f, h * 0.6f, w * 0.1f, h * 0.45f, w * 0.1f, h * 0.32f)
            cubicTo(w * 0.1f, h * 0.15f, w * 0.28f, h * 0.05f, w * 0.5f, h * 0.05f)
            cubicTo(w * 0.72f, h * 0.05f, w * 0.9f, h * 0.15f, w * 0.9f, h * 0.32f)
            cubicTo(w * 0.9f, h * 0.45f, w * 0.8f, h * 0.6f, w * 0.5f, h * 0.95f)
            close()
        }
        drawPath(
            path = path, color = tint, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
        drawCircle(
            color = tint, radius = w * 0.14f, center = Offset(w * 0.5f, h * 0.32f)
        )
    }
}

@Composable
private fun ChevronDownIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.35f)
            lineTo(w * 0.5f, h * 0.65f)
            lineTo(w * 0.8f, h * 0.35f)
        }
        drawPath(
            path = path, color = tint, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun SearchHeaderIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val radius = w * 0.32f
        val center = Offset(w * 0.4f, h * 0.4f)

        drawCircle(
            color = tint, radius = radius, center = center, style = Stroke(width = 2.5.dp.toPx())
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.62f, h * 0.62f),
            end = Offset(w * 0.9f, h * 0.9f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun DropletMetricIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF1A60B7)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            cubicTo(w * 0.85f, h * 0.5f, w * 0.9f, h * 0.7f, w * 0.9f, h * 0.75f)
            cubicTo(w * 0.9f, h * 0.92f, w * 0.72f, h * 0.98f, w * 0.5f, h * 0.98f)
            cubicTo(w * 0.28f, h * 0.98f, w * 0.1f, h * 0.92f, w * 0.1f, h * 0.75f)
            cubicTo(w * 0.1f, h * 0.5f, w * 0.15f, h * 0.5f, w * 0.5f, h * 0.1f)
            close()
        }
        drawPath(
            path = path, color = tint, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        val innerPath = Path().apply {
            moveTo(w * 0.42f, h * 0.6f)
            cubicTo(w * 0.35f, h * 0.68f, w * 0.35f, h * 0.78f, w * 0.42f, h * 0.82f)
        }
        drawPath(
            path = innerPath,
            color = tint,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun WindMetricIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF1A60B7)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()

        val p1 = Path().apply {
            moveTo(w * 0.1f, h * 0.3f)
            lineTo(w * 0.65f, h * 0.3f)
            cubicTo(w * 0.85f, h * 0.3f, w * 0.85f, h * 0.1f, w * 0.7f, h * 0.1f)
        }
        drawPath(p1, color = tint, style = Stroke(stroke, cap = StrokeCap.Round))

        val p2 = Path().apply {
            moveTo(w * 0.05f, h * 0.55f)
            lineTo(w * 0.8f, h * 0.55f)
            cubicTo(w * 0.95f, h * 0.55f, w * 0.95f, h * 0.35f, w * 0.82f, h * 0.35f)
        }
        drawPath(p2, color = tint, style = Stroke(stroke, cap = StrokeCap.Round))

        val p3 = Path().apply {
            moveTo(w * 0.2f, h * 0.8f)
            lineTo(w * 0.55f, h * 0.8f)
            cubicTo(w * 0.7f, h * 0.8f, w * 0.7f, h * 0.95f, w * 0.58f, h * 0.95f)
        }
        drawPath(p3, color = tint, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

@Composable
private fun PressureMetricIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF1A60B7)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.5f)
        val radius = w * 0.4f

        drawCircle(
            color = tint, radius = radius, center = center, style = Stroke(width = 2.dp.toPx())
        )

        val arrowPath = Path().apply {
            moveTo(center.x, center.y)
            lineTo(center.x - (radius * 0.45f), center.y - (radius * 0.45f))
        }
        drawPath(
            arrowPath, color = tint, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        drawCircle(
            color = tint, radius = 2.5.dp.toPx(), center = center
        )

        val t1 = Offset(center.x - (radius * 0.6f), center.y + (radius * 0.1f))
        val t2 = Offset(center.x + (radius * 0.6f), center.y + (radius * 0.1f))
        drawCircle(tint, radius = 1.dp.toPx(), center = t1)
        drawCircle(tint, radius = 1.dp.toPx(), center = t2)
    }
}

@Composable
private fun VisibilityMetricIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF1A60B7)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.5f, h * 0.5f)

        val eyePath = Path().apply {
            moveTo(w * 0.08f, center.y)
            cubicTo(w * 0.25f, h * 0.2f, w * 0.75f, h * 0.2f, w * 0.92f, center.y)
            cubicTo(w * 0.75f, h * 0.8f, w * 0.25f, h * 0.8f, w * 0.08f, center.y)
            close()
        }
        drawPath(
            path = eyePath, color = tint, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        drawCircle(
            color = tint, radius = w * 0.16f, center = center, style = Stroke(width = 2.dp.toPx())
        )

        drawCircle(
            color = tint, radius = w * 0.07f, center = center
        )
    }
}

@Composable
private fun HomeNavIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF1A60B7)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val housePath = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            lineTo(w * 0.9f, h * 0.45f)
            lineTo(w * 0.9f, h * 0.9f)
            lineTo(w * 0.62f, h * 0.9f)
            lineTo(w * 0.62f, h * 0.62f)
            lineTo(w * 0.38f, h * 0.62f)
            lineTo(w * 0.38f, h * 0.9f)
            lineTo(w * 0.1f, h * 0.9f)
            lineTo(w * 0.1f, h * 0.45f)
            close()
        }
        drawPath(path = housePath, color = tint)
    }
}

@Composable
private fun SearchNavIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF8B9BB0)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val radius = w * 0.32f
        val center = Offset(w * 0.42f, h * 0.42f)

        drawCircle(
            color = tint, radius = radius, center = center, style = Stroke(width = 2.dp.toPx())
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.64f, h * 0.64f),
            end = Offset(w * 0.88f, h * 0.88f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun HeartNavIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFF8B9BB0)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val heartPath = Path().apply {
            moveTo(w * 0.5f, h * 0.85f)
            cubicTo(w * 0.1f, h * 0.55f, w * 0.05f, h * 0.3f, w * 0.25f, h * 0.15f)
            cubicTo(w * 0.4f, h * 0.05f, w * 0.5f, h * 0.25f, w * 0.5f, h * 0.25f)
            cubicTo(w * 0.5f, h * 0.25f, w * 0.6f, h * 0.05f, w * 0.75f, h * 0.15f)
            cubicTo(w * 0.95f, h * 0.3f, w * 0.9f, h * 0.55f, w * 0.5f, h * 0.85f)
            close()
        }
        drawPath(
            path = heartPath,
            color = tint,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}


@Composable
fun WeatherBackgroundView(
    background: WeatherBackground, modifier: Modifier = Modifier
) {
    val image = when (background) {
        WeatherBackground.CLEAR_MORNING -> Res.drawable.clear_morning
        WeatherBackground.CLEAR_AFTERNOON -> Res.drawable.clear_afternoon
        WeatherBackground.CLEAR_EVENING -> Res.drawable.clear_evening
        WeatherBackground.CLEAR_NIGHT -> Res.drawable.clear_night
        WeatherBackground.CLOUDY -> Res.drawable.cloudy
        WeatherBackground.HEAVY_CLOUDS -> Res.drawable.heavy_cloud
        WeatherBackground.RAIN -> Res.drawable.rain
        WeatherBackground.NIGHT_RAIN -> Res.drawable.night_rain
        WeatherBackground.THUNDERSTORM -> Res.drawable.thunder_storm
        WeatherBackground.SNOW -> Res.drawable.snow
        WeatherBackground.FOG -> Res.drawable.fog
        WeatherBackground.PARTLY_CLOUDY -> Res.drawable.partially_cloud
    }

    Box(modifier = modifier) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark overlay keeps white weather text readable.
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.12f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.30f)
                    )
                )
            )
        )
    }
}
