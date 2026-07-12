package com.izzatismail.mealmap.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class PantryItemRequest(
    @field:NotBlank
    val name: String,

    @field:Positive
    val amount: Double,

    @field:NotBlank
    val unit: String,

    val expirationDate: LocalDate? = null,
)

data class UpdatePantryItemRequest(
    @field:Positive
    val amount: Double,

    @field:NotBlank
    val unit: String,

    val expirationDate: LocalDate? = null,
)