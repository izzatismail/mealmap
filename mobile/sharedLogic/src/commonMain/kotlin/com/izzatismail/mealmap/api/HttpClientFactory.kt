package com.izzatismail.mealmap.api

import io.ktor.client.HttpClient

expect object HttpClientFactory {
    fun create(): HttpClient
}