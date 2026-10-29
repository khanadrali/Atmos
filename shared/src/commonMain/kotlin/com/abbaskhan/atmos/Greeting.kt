package com.abbaskhan.atmos

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello("Abbas Khan From "+platform.name)
    }
}