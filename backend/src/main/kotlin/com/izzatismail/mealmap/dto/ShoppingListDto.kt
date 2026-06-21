package com.izzatismail.mealmap.dto

import java.time.LocalDateTime

data class ShoppingListDto(
    val id: Long,
    val mealPlanId: Long,
    val userId: Long,
    val generatedAt: LocalDateTime,
    val items: List<ShoppingItemDto> = emptyList(),
)