package com.abbaskhan.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(
    engine: HttpClientEngine,
    json: Json,
    baseUrl: String
): HttpClient {
    return HttpClient(engine) {

        expectSuccess = false

        install(ContentNegotiation) {
            json(json)
        }

        defaultRequest {
            url(baseUrl)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }

        install(Logging) {
            logger = HttpLogger
            level = LogLevel.INFO

            sanitizeHeader { header ->
                header.equals(
                    HttpHeaders.Authorization,
                    ignoreCase = true
                ) || header.equals(
                    "X-API-Key",
                    ignoreCase = true
                )
            }
        }
    }
}

