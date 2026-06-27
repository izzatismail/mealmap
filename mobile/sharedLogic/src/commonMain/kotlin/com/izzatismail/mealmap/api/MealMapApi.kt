package com.izzatismail.mealmap.api

import com.izzatismail.mealmap.model.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MealMapApi(
    private val httpClient: HttpClient,
) {
    suspend fun searchRecipes(query: String, limit: Int = 10): List<Recipe> {
        return httpClient.get("${ApiConfig.baseUrl}/api/recipes/search") {
            parameter("query", query)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getRecipeById(id: Long): Recipe {
        return httpClient.get("${ApiConfig.baseUrl}/api/recipes/$id").body()
    }

    suspend fun findByIngredients(ingredients: List<String>): List<Recipe> {
        return httpClient.get("${ApiConfig.baseUrl}/api/recipes/by-ingredients") {
            parameter("ingredients", ingredients)
        }.body()
    }

    suspend fun getCachedRecipes(): List<Recipe> {
        return httpClient.get("${ApiConfig.baseUrl}/api/recipes/cached").body()
    }
}