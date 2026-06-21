package com.izzatismail.mealmap.dto

import com.izzatismail.mealmap.entity.Ingredient

data class IngredientDto(
    val id: Long,
    val name: String,
    val unit: String,
    val aisle: String,
) {
    companion object {
        fun fromEntity(ingredient: Ingredient) = IngredientDto(
            id = ingredient.id,
            name = ingredient.name,
            unit = ingredient.unit,
            aisle = ingredient.aisle,
        )
    }
}