package com.izzatismail.mealmap.api

interface TokenProvider {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
    suspend fun hasToken(): Boolean
}