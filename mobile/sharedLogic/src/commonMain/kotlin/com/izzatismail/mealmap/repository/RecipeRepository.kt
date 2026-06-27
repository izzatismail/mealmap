package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.api.MealMapApi
import com.izzatismail.mealmap.database.CachedRecipe
import com.izzatismail.mealmap.database.MealMapDatabase
import com.izzatismail.mealmap.model.Recipe
import com.izzatismail.mealmap.model.SpoonacularIngredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import com.izzatismail.mealmap.database.currentTimeMillis
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

open class RecipeRepository(
    private val api: MealMapApi?,
    private val database: MealMapDatabase?,
) {
    private val json = Json { ignoreUnknownKeys = true }

    open suspend fun searchRecipes(query: String, limit: Int = 10): List<Recipe> {
        return withContext(Dispatchers.IO) {
            try {
                val recipes = requireNotNull(api) { "API not configured" }.searchRecipes(query, limit)
                cacheRecipes(recipes)
                recipes
            } catch (_: Exception) {
                database?.recipeCacheQueries?.searchByTitle(query)?.executeAsList()?.map { it.toRecipe() }
                    ?: throw Exception("No network or cache available")
            }
        }
    }

    open suspend fun getRecipeById(id: Long): Recipe {
        return withContext(Dispatchers.IO) {
            try {
                val recipe = requireNotNull(api) { "API not configured" }.getRecipeById(id)
                cacheRecipes(listOf(recipe))
                recipe
            } catch (_: Exception) {
                database?.recipeCacheQueries?.getById(id)?.executeAsOneOrNull()?.toRecipe()
                    ?: throw Exception("Recipe not found")
            }
        }
    }

    open suspend fun getCachedRecipes(): List<Recipe> {
        return withContext(Dispatchers.IO) {
            database?.recipeCacheQueries?.getAll()?.executeAsList()?.map { it.toRecipe() }
                ?: emptyList()
        }
    }

    private fun cacheRecipes(recipes: List<Recipe>) {
        val db = database ?: return
        val now = currentTimeMillis()
        db.recipeCacheQueries.transaction {
            for (recipe in recipes) {
                db.recipeCacheQueries.insert(
                    CachedRecipe(
                        spoonacularId = recipe.spoonacularId,
                        title = recipe.title,
                        image = recipe.image,
                        imageType = recipe.imageType,
                        servings = recipe.servings.toLong(),
                        readyInMinutes = recipe.readyInMinutes.toLong(),
                        sourceUrl = recipe.sourceUrl,
                        sourceName = recipe.sourceName,
                        creditsText = recipe.creditsText,
                        summary = recipe.summary,
                        instructions = recipe.instructions,
                        healthScore = recipe.healthScore,
                        spoonacularScore = recipe.spoonacularScore,
                        pricePerServing = recipe.pricePerServing,
                        cheap = if (recipe.cheap) 1L else 0L,
                        dairyFree = if (recipe.dairyFree) 1L else 0L,
                        glutenFree = if (recipe.glutenFree) 1L else 0L,
                        vegan = if (recipe.vegan) 1L else 0L,
                        vegetarian = if (recipe.vegetarian) 1L else 0L,
                        veryHealthy = if (recipe.veryHealthy) 1L else 0L,
                        veryPopular = if (recipe.veryPopular) 1L else 0L,
                        sustainable = if (recipe.sustainable) 1L else 0L,
                        whole30 = if (recipe.whole30) 1L else 0L,
                        cuisines = json.encodeToString(recipe.cuisines),
                        dishTypes = json.encodeToString(recipe.dishTypes),
                        diets = json.encodeToString(recipe.diets),
                        ingredients = json.encodeToString(recipe.ingredients),
                        cachedAt = now,
                    )
                )
            }
        }
    }

    private fun CachedRecipe.toRecipe(): Recipe {
        return Recipe(
            spoonacularId = spoonacularId,
            title = title,
            image = image,
            imageType = imageType,
            servings = servings.toInt(),
            readyInMinutes = readyInMinutes.toInt(),
            sourceUrl = sourceUrl,
            sourceName = sourceName,
            creditsText = creditsText,
            summary = summary,
            instructions = instructions,
            healthScore = healthScore,
            spoonacularScore = spoonacularScore,
            pricePerServing = pricePerServing,
            cheap = cheap == 1L,
            dairyFree = dairyFree == 1L,
            glutenFree = glutenFree == 1L,
            vegan = vegan == 1L,
            vegetarian = vegetarian == 1L,
            veryHealthy = veryHealthy == 1L,
            veryPopular = veryPopular == 1L,
            sustainable = sustainable == 1L,
            whole30 = whole30 == 1L,
            cuisines = decodeJsonList(cuisines),
            dishTypes = decodeJsonList(dishTypes),
            diets = decodeJsonList(diets),
            ingredients = decodeIngredients(ingredients),
        )
    }

    private fun decodeJsonList(jsonString: String): List<String> {
        if (jsonString.isBlank()) return emptyList()
        return try {
            json.decodeFromString<List<String>>(jsonString)
        } catch (_: Exception) { emptyList() }
    }

    private fun decodeIngredients(jsonString: String): List<SpoonacularIngredient> {
        if (jsonString.isBlank()) return emptyList()
        return try {
            json.decodeFromString<List<SpoonacularIngredient>>(jsonString)
        } catch (_: Exception) { emptyList() }
    }
}