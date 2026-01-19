package com.nexable.smartcookly

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform