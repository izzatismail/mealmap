package com.izzatismail.mealmap.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val spoonacularId: Long,
    val title: String,
    val image: String,
    val imageType: String = "",
    val servings: Int = 0,
    val readyInMinutes: Int = 0,
    val sourceUrl: String = "",
    val sourceName: String = "",
    val creditsText: String = "",
    val summary: String = "",
    val instructions: String = "",
    val healthScore: Double = 0.0,
    val spoonacularScore: Double = 0.0,
    val pricePerServing: Double = 0.0,
    val cheap: Boolean = false,
    val dairyFree: Boolean = false,
    val glutenFree: Boolean = false,
    val vegan: Boolean = false,
    val vegetarian: Boolean = false,
    val veryHealthy: Boolean = false,
    val veryPopular: Boolean = false,
    val sustainable: Boolean = false,
    val whole30: Boolean = false,
    val cuisines: List<String> = emptyList(),
    val dishTypes: List<String> = emptyList(),
    val diets: List<String> = emptyList(),
    val ingredients: List<SpoonacularIngredient> = emptyList(),
)