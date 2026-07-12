package com.izzatismail.mealmap.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class AddPlannedMealRequest(
    @field:NotNull
    val recipeId: Long,

    @field:NotNull
    val mealType: String,

    @field:NotNull @field:Min(0) @field:Max(6)
    val dayOfWeek: Int,

    @field:NotNull @field:Positive
    val servings: Int,
)