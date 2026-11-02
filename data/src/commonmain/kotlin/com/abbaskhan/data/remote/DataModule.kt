package com.abbaskhan.data.remote



import com.abbaskhan.data.ApiConfig
import com.abbaskhan.data.remote.datasource.WeatherRemoteDataSource
import com.abbaskhan.data.repository.WeatherRepositoryImpl
import com.abbaskhan.domain.repository.WeatherRepository
import com.abbaskhan.domain.usecase.GetCurrentWeatherUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import org.koin.dsl.module


expect fun createPlatformEngine(): HttpClientEngine

val dataModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single<HttpClientEngine> {
        createPlatformEngine()
    }

    single<HttpClient> {
        createHttpClient(
            engine = get(),
            json = get(),
            baseUrl = ApiConfig.BASE_URL
        )
    }

    single {
        WeatherRemoteDataSource(
            client = get()
        )
    }

    single<WeatherRepository> {
        WeatherRepositoryImpl(
            remoteDataSource = get()
        )
    }

    single {
        GetCurrentWeatherUseCase(
            weatherRepository = get()
        )
    }
}