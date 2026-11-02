package com.abbaskhan.data.repository

import com.abbaskhan.data.mapper.toDomain
import com.abbaskhan.data.remote.datasource.WeatherRemoteDataSource
import com.abbaskhan.domain.ApiResponse
import com.abbaskhan.domain.AppError
import com.abbaskhan.domain.model.Weather
import com.abbaskhan.domain.repository.WeatherRepository

class WeatherRepositoryImpl(private val remoteDataSource: WeatherRemoteDataSource) :
    WeatherRepository {

    override suspend fun getCurrentWeather(
        cityName: String
    ): ApiResponse<Weather> {
        return try {
            val city = remoteDataSource.searchCity(cityName).results.firstOrNull()
                ?: return ApiResponse.Error(AppError.NotFound)

            val response = remoteDataSource.getCurrentWeather(
                latitude = city.latitude, longitude = city.longitude
            )

            ApiResponse.Success(
                response.toDomain(cityName = city.name)
            )
        }
        catch (e: Exception) {
                println("WEATHER ERROR TYPE: ${e::class.simpleName}")
                println("WEATHER ERROR MESSAGE: ${e.message}")
                e.printStackTrace()
                ApiResponse.Error(AppError.Network)
        }
    }

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        cityName: String
    ): ApiResponse<Weather> {
        return try {
            val response = remoteDataSource.getCurrentWeather(
                latitude = latitude, longitude = longitude
            )
            ApiResponse.Success(
                response.toDomain(cityName = cityName)
            )
        } catch (e: Exception) {
            println("WEATHER ERROR TYPE: ${e::class.simpleName}")
            println("WEATHER ERROR MESSAGE: ${e.message}")
            e.printStackTrace()
            ApiResponse.Error(AppError.Network)
        }
    }
}
