package com.abbaskhan.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    val results: List<GeocodingResultDto> = emptyList()
)

@Serializable
data class GeocodingResultDto(
    val name: String,
    val latitude: Double,
    val longitude: Double
)