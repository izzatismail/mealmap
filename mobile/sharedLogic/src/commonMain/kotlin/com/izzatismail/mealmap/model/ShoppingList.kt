package com.izzatismail.mealmap.model

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListDto(
    val id: Long,
    val mealPlanId: Long,
    val userId: Long,
    val generatedAt: String = "",
    val items: List<ShoppingItemDto> = emptyList(),
)

@Serializable
data class ShoppingItemDto(
    val id: Long,
    val name: String,
    val amount: Double,
    val unit: String,
    val isChecked: Boolean = false,
    val category: String = "",
    val ingredientId: Long? = null,
)