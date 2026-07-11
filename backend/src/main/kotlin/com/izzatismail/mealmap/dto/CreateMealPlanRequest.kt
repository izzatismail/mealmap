package com.izzatismail.mealmap.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CreateMealPlanRequest(
    @field:NotNull @field:FutureOrPresent
    val weekStart: LocalDate,

    @field:NotNull @field:FutureOrPresent
    val weekEnd: LocalDate,

    @field:NotEmpty
    @field:Valid
    val plannedMeals: List<CreatePlannedMealRequest>,
)

data class CreatePlannedMealRequest(
    @field:NotNull
    val recipeId: Long,

    @field:NotNull
    val mealType: String,

    @field:NotNull
    val dayOfWeek: Int,

    @field:NotNull
    val servings: Int,
)