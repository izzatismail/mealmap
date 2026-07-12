package com.izzatismail.mealmap.model

import kotlinx.serialization.Serializable

@Serializable
data class PantryItemDto(
    val id: Long,
    val userId: Long,
    val name: String,
    val amount: Double,
    val unit: String,
    val expirationDate: String? = null,
    val addedAt: String = "",
    val ingredientId: Long? = null,
)

@Serializable
data class PantryItemRequest(
    val name: String,
    val amount: Double,
    val unit: String,
    val expirationDate: String? = null,
)