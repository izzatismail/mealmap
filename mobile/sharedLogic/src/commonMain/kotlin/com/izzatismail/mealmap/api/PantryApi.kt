package com.izzatismail.mealmap.api

import com.izzatismail.mealmap.model.PantryItemDto
import com.izzatismail.mealmap.model.PantryItemRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PantryApi(
    private val httpClient: HttpClient,
) {
    suspend fun getPantryItems(): List<PantryItemDto> {
        return httpClient.get("${ApiConfig.baseUrl}/api/pantry").body()
    }

    suspend fun addPantryItem(request: PantryItemRequest): PantryItemDto {
        return httpClient.post("${ApiConfig.baseUrl}/api/pantry") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updatePantryItem(id: Long, request: PantryItemUpdateRequest): PantryItemDto {
        return httpClient.put("${ApiConfig.baseUrl}/api/pantry/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deletePantryItem(id: Long) {
        httpClient.delete("${ApiConfig.baseUrl}/api/pantry/$id")
    }
}

@kotlinx.serialization.Serializable
data class PantryItemUpdateRequest(
    val amount: Double,
    val unit: String,
)