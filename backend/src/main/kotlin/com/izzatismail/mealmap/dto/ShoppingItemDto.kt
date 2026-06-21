package com.izzatismail.mealmap.dto

data class ShoppingItemDto(
    val id: Long,
    val name: String,
    val amount: Double,
    val unit: String,
    val isChecked: Boolean,
    val category: String,
    val ingredientId: Long?,
)