package com.abbaskhan.data.remote

import io.ktor.client.plugins.logging.Logger


object HttpLogger : Logger {
    override fun log(message: String) {
        println("Atmos HTTP: $message")
    }
}