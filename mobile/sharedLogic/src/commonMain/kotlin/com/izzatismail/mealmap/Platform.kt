package com.izzatismail.mealmap

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform