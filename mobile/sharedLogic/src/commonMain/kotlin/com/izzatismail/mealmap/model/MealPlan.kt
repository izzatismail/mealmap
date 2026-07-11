package com.izzatismail.mealmap.model

import kotlinx.serialization.Serializable

@Serializable
data class MealPlanDto(
    val id: Long,
    val userId: Long,
    val weekStart: String,
    val weekEnd: String,
    val createdAt: String = "",
    val plannedMeals: List<PlannedMealDto> = emptyList(),
)

@Serializable
data class PlannedMealDto(
    val id: Long,
    val recipeId: Long,
    val recipeTitle: String = "",
    val mealType: String,
    val dayOfWeek: Int,
    val servings: Int = 1,
)

@Serializable
data class MealPlanRequest(
    val weekStart: String,
    val weekEnd: String,
    val plannedMeals: List<PlannedMealRequest>,
)

@Serializable
data class PlannedMealRequest(
    val recipeId: Long,
    val mealType: String,
    val dayOfWeek: Int,
    val servings: Int,
)