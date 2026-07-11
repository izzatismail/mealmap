package com.izzatismail.mealmap.api

import com.izzatismail.mealmap.model.ShoppingItemDto
import com.izzatismail.mealmap.model.ShoppingListDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class ShoppingListApi(
    private val httpClient: HttpClient,
) {
    suspend fun generateShoppingList(mealPlanId: Long, regenerate: Boolean = false): ShoppingListDto {
        return httpClient.post("${ApiConfig.baseUrl}/api/meal-plans/$mealPlanId/shopping-list") {
            parameter("regenerate", regenerate)
        }.body()
    }

    suspend fun getShoppingList(id: Long): ShoppingListDto {
        return httpClient.get("${ApiConfig.baseUrl}/api/shopping-lists/$id").body()
    }

    suspend fun getCurrentShoppingList(): ShoppingListDto? {
        return try {
            httpClient.get("${ApiConfig.baseUrl}/api/shopping-lists/current").body()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun toggleItem(itemId: Long): ShoppingItemDto {
        return httpClient.patch("${ApiConfig.baseUrl}/api/shopping-items/$itemId/toggle").body()
    }
}