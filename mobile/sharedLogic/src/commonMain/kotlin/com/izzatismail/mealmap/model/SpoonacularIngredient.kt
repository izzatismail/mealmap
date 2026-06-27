package com.izzatismail.mealmap.model

import kotlinx.serialization.Serializable

@Serializable
data class SpoonacularIngredient(
    val id: Long = 0,
    val name: String = "",
    val original: String = "",
    val amount: Double = 0.0,
    val unit: String = "",
    val aisle: String = "",
    val image: String = "",
)