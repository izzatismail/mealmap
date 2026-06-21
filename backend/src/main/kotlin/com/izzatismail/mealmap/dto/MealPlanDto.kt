package com.izzatismail.mealmap.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class MealPlanDto(
    val id: Long,
    val userId: Long,
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val createdAt: LocalDateTime,
    val plannedMeals: List<PlannedMealDto> = emptyList(),
)