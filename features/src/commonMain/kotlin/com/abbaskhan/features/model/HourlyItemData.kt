package com.abbaskhan.features.model

data class HourlyItemData(
    val time: String,
    val temp: String,
    val weatherCode: Int,
    val isDay: Boolean
)