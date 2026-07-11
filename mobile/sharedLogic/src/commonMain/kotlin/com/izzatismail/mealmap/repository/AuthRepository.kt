package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.api.AuthApi
import com.izzatismail.mealmap.api.TokenProvider
import com.izzatismail.mealmap.model.AuthResponse
import com.izzatismail.mealmap.model.LoginRequest
import com.izzatismail.mealmap.model.RegisterRequest

open class AuthRepository(
    private val authApi: AuthApi,
    private val tokenProvider: TokenProvider,
) {
    open suspend fun login(email: String, password: String): AuthResponse {
        val response = authApi.login(LoginRequest(email = email, password = password))
        tokenProvider.saveToken(response.token)
        return response
    }

    open suspend fun register(email: String, password: String, name: String): AuthResponse {
        val response = authApi.register(RegisterRequest(email = email, password = password, name = name))
        tokenProvider.saveToken(response.token)
        return response
    }

    open suspend fun isLoggedIn(): Boolean = tokenProvider.hasToken()

    open suspend fun logout() {
        tokenProvider.clearToken()
    }

    open suspend fun getToken(): String? = tokenProvider.getToken()
}