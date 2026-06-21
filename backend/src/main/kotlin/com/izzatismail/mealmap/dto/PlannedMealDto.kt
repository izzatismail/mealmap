package com.izzatismail.mealmap.dto

import com.izzatismail.mealmap.entity.MealType
import java.time.LocalDate

data class PlannedMealDto(
    val id: Long,
    val recipeId: Long,
    val recipeTitle: String,
    val mealType: MealType,
    val dayOfWeek: Int,
    val servings: Int,
)