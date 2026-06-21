package com.izzatismail.mealmap.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class PantryItemDto(
    val id: Long,
    val userId: Long,
    val name: String,
    val amount: Double,
    val unit: String,
    val expirationDate: LocalDate?,
    val addedAt: LocalDateTime,
    val ingredientId: Long?,
)