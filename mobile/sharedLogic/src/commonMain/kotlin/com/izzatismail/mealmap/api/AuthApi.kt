package com.izzatismail.mealmap.api

import com.izzatismail.mealmap.model.AuthResponse
import com.izzatismail.mealmap.model.LoginRequest
import com.izzatismail.mealmap.model.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApi(
    private val httpClient: HttpClient,
) {
    suspend fun register(request: RegisterRequest): AuthResponse {
        return httpClient.post("${ApiConfig.baseUrl}/api/auth/register") {
            setBody(request)
        }.body()
    }

    suspend fun login(request: LoginRequest): AuthResponse {
        return httpClient.post("${ApiConfig.baseUrl}/api/auth/login") {
            setBody(request)
        }.body()
    }
}