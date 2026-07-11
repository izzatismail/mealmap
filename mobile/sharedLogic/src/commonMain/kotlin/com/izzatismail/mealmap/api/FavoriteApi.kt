package com.izzatismail.mealmap.api

import com.izzatismail.mealmap.model.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class FavoriteApi(
    private val httpClient: HttpClient,
) {
    suspend fun getFavorites(): List<Recipe> {
        return httpClient.get("${ApiConfig.baseUrl}/api/favorites").body()
    }

    suspend fun getFavoriteIds(): List<Long> {
        return httpClient.get("${ApiConfig.baseUrl}/api/favorites/ids").body()
    }

    suspend fun isFavorited(recipeId: Long): Boolean {
        val response = httpClient.get("${ApiConfig.baseUrl}/api/favorites/check/$recipeId")
        val map = response.body<Map<String, Boolean>>()
        return map["favorited"] ?: false
    }

    suspend fun addFavorite(recipeId: Long): Recipe {
        return httpClient.post("${ApiConfig.baseUrl}/api/favorites/$recipeId").body()
    }

    suspend fun removeFavorite(recipeId: Long) {
        httpClient.delete("${ApiConfig.baseUrl}/api/favorites/$recipeId")
    }
}