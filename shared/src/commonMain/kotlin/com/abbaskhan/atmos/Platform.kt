package com.abbaskhan.atmos

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform