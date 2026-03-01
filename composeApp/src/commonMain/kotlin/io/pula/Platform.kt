package io.pula

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform