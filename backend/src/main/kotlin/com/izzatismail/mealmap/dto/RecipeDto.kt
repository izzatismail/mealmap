package com.izzatismail.mealmap.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.izzatismail.mealmap.entity.Recipe
import org.slf4j.LoggerFactory

@JsonIgnoreProperties(ignoreUnknown = true)
data class RecipeDto(
    val spoonacularId: Long,
    val title: String,
    val image: String,
    val imageType: String,
    val servings: Int,
    val readyInMinutes: Int,
    val sourceUrl: String,
    val sourceName: String,
    val creditsText: String,
    val summary: String,
    val instructions: String,
    val healthScore: Double,
    val spoonacularScore: Double,
    val pricePerServing: Double,
    val cheap: Boolean,
    val dairyFree: Boolean,
    val glutenFree: Boolean,
    val vegan: Boolean,
    val vegetarian: Boolean,
    val veryHealthy: Boolean,
    val veryPopular: Boolean,
    val sustainable: Boolean,
    val whole30: Boolean,
    val cuisines: List<String> = emptyList(),
    val dishTypes: List<String> = emptyList(),
    val diets: List<String> = emptyList(),
    val ingredients: List<SpoonacularIngredient> = emptyList(),
) {
    companion object {
        private val log = LoggerFactory.getLogger(RecipeDto::class.java)
        private val objectMapper = ObjectMapper()

        fun fromEntity(recipe: Recipe): RecipeDto {
            val cuisines = parseJsonList(recipe.cuisines)
            val dishTypes = parseJsonList(recipe.dishTypes)
            val diets = parseJsonList(recipe.diets)
            val ingredients = parseIngredients(recipe.ingredients)

            return RecipeDto(
                spoonacularId = recipe.spoonacularId,
                title = recipe.title,
                image = recipe.image,
                imageType = recipe.imageType,
                servings = recipe.servings,
                readyInMinutes = recipe.readyInMinutes,
                sourceUrl = recipe.sourceUrl,
                sourceName = recipe.sourceName,
                creditsText = recipe.creditsText,
                summary = recipe.summary,
                instructions = recipe.instructions,
                healthScore = recipe.healthScore,
                spoonacularScore = recipe.spoonacularScore,
                pricePerServing = recipe.pricePerServing,
                cheap = recipe.cheap,
                dairyFree = recipe.dairyFree,
                glutenFree = recipe.glutenFree,
                vegan = recipe.vegan,
                vegetarian = recipe.vegetarian,
                veryHealthy = recipe.veryHealthy,
                veryPopular = recipe.veryPopular,
                sustainable = recipe.sustainable,
                whole30 = recipe.whole30,
                cuisines = cuisines,
                dishTypes = dishTypes,
                diets = diets,
                ingredients = ingredients,
            )
        }

        private fun parseJsonList(json: String): List<String> {
            if (json.isBlank()) return emptyList()
            return try {
                objectMapper.readValue(json, object : TypeReference<List<String>>() {})
            } catch (e: Exception) {
                log.warn("Failed to parse JSON list from stored recipe data: {}", e.message)
                emptyList()
            }
        }

        private fun parseIngredients(json: String): List<SpoonacularIngredient> {
            if (json.isBlank()) return emptyList()
            return try {
                objectMapper.readValue(json, object : TypeReference<List<SpoonacularIngredient>>() {})
            } catch (e: Exception) {
                log.warn("Failed to parse ingredients from stored recipe data: {}", e.message)
                emptyList()
            }
        }
    }
}