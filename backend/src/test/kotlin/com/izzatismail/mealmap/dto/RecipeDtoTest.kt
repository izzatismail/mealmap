package com.izzatismail.mealmap.dto

import com.izzatismail.mealmap.entity.Recipe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class RecipeDtoTest {

    @Test
    fun `fromEntity maps all fields correctly`() {
        val now = LocalDateTime.now()
        val recipe = Recipe(
            spoonacularId = 12345L,
            title = "Test Recipe",
            image = "https://example.com/image.jpg",
            imageType = "jpg",
            servings = 4,
            readyInMinutes = 30,
            sourceUrl = "https://example.com/recipe",
            sourceName = "Test Source",
            creditsText = "Test Credit",
            summary = "<p>Delicious test recipe</p>",
            instructions = "Step 1: Do something",
            healthScore = 80.0,
            spoonacularScore = 90.0,
            pricePerServing = 2.50,
            cheap = true,
            dairyFree = false,
            glutenFree = true,
            vegan = false,
            vegetarian = true,
            veryHealthy = false,
            veryPopular = true,
            sustainable = false,
            whole30 = true,
            cuisines = """["Italian","Mexican"]""",
            dishTypes = """["lunch","dinner"]""",
            diets = """["vegetarian"]""",
            ingredients = """[{"id":1001,"name":"butter","original":"1 tbsp butter","amount":1.0,"unit":"tbsp","aisle":"Baking","image":"butter.jpg"}]""",
            cachedAt = now,
        )

        val dto = RecipeDto.fromEntity(recipe)

        assertEquals(12345L, dto.spoonacularId)
        assertEquals("Test Recipe", dto.title)
        assertEquals("https://example.com/image.jpg", dto.image)
        assertEquals("jpg", dto.imageType)
        assertEquals(4, dto.servings)
        assertEquals(30, dto.readyInMinutes)
        assertEquals("https://example.com/recipe", dto.sourceUrl)
        assertEquals("Test Source", dto.sourceName)
        assertEquals("Test Credit", dto.creditsText)
        assertEquals("<p>Delicious test recipe</p>", dto.summary)
        assertEquals("Step 1: Do something", dto.instructions)
        assertEquals(80.0, dto.healthScore)
        assertEquals(90.0, dto.spoonacularScore)
        assertEquals(2.50, dto.pricePerServing)
        assertEquals(true, dto.cheap)
        assertEquals(false, dto.dairyFree)
        assertEquals(true, dto.glutenFree)
        assertEquals(false, dto.vegan)
        assertEquals(true, dto.vegetarian)
        assertEquals(false, dto.veryHealthy)
        assertEquals(true, dto.veryPopular)
        assertEquals(false, dto.sustainable)
        assertEquals(true, dto.whole30)
        assertEquals(listOf("Italian", "Mexican"), dto.cuisines)
        assertEquals(listOf("lunch", "dinner"), dto.dishTypes)
        assertEquals(listOf("vegetarian"), dto.diets)
        assertEquals(1, dto.ingredients.size)
        assertEquals("butter", dto.ingredients[0].name)
        assertEquals("1 tbsp butter", dto.ingredients[0].original)
        assertEquals(1.0, dto.ingredients[0].amount)
        assertEquals("tbsp", dto.ingredients[0].unit)
    }

    @Test
    fun `fromEntity returns empty lists for blank JSON strings`() {
        val recipe = Recipe(
            spoonacularId = 1L,
            title = "Simple",
            cuisines = "",
            dishTypes = "",
            diets = "",
            ingredients = "",
        )

        val dto = RecipeDto.fromEntity(recipe)

        assertTrue(dto.cuisines.isEmpty())
        assertTrue(dto.dishTypes.isEmpty())
        assertTrue(dto.diets.isEmpty())
        assertTrue(dto.ingredients.isEmpty())
    }

    @Test
    fun `fromEntity returns empty lists for malformed JSON`() {
        val recipe = Recipe(
            spoonacularId = 2L,
            title = "Corrupted",
            cuisines = "not-json",
            dishTypes = "[broken",
            ingredients = "{invalid",
        )

        val dto = RecipeDto.fromEntity(recipe)

        assertTrue(dto.cuisines.isEmpty())
        assertTrue(dto.dishTypes.isEmpty())
        assertTrue(dto.ingredients.isEmpty())
    }
}