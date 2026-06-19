package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.dto.spoonacular.SpoonacularFindByIngredientsResult
import com.izzatismail.mealmap.dto.spoonacular.SpoonacularRecipeResponse
import com.izzatismail.mealmap.dto.spoonacular.SpoonacularSearchResult
import com.izzatismail.mealmap.entity.Recipe
import com.izzatismail.mealmap.exception.SpoonacularApiException
import com.izzatismail.mealmap.repository.RecipeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@SpringBootTest
@ActiveProfiles("test")
class SpoonacularServiceTest {

    @Autowired
    private lateinit var spoonacularService: SpoonacularService

    @MockBean
    private lateinit var recipeRepository: RecipeRepository

    @MockBean
    private lateinit var restTemplate: RestTemplate

    @Test
    fun `searchRecipes returns cached results when enough in DB`() {
        val cached = listOf(
            Recipe(spoonacularId = 1L, title = "Pasta Primavera"),
            Recipe(spoonacularId = 2L, title = "Pasta Carbonara"),
        )
        whenever(recipeRepository.findByTitleContainingIgnoreCase("pasta")).thenReturn(cached)

        val results = spoonacularService.searchRecipes("pasta", 2)

        assertEquals(2, results.size)
        assertEquals("Pasta Primavera", results[0].title)
        assertEquals("Pasta Carbonara", results[1].title)
        verify(restTemplate, never()).getForObject(any<String>(), any<Class<*>>())
    }

    @Test
    fun `searchRecipes calls API when not enough in cache`() {
        whenever(recipeRepository.findByTitleContainingIgnoreCase("sushi")).thenReturn(emptyList())

        val searchResult = SpoonacularSearchResult(
            results = listOf(
                SpoonacularSearchResult.RecipeSummary(id = 100L, title = "Sushi Roll", image = "img.jpg", imageType = "jpg"),
            )
        )
        whenever(restTemplate.getForObject(any<String>(), eq(SpoonacularSearchResult::class.java))).thenReturn(searchResult)
        whenever(recipeRepository.findBySpoonacularId(100L)).thenReturn(null)
        whenever(recipeRepository.save(any<Recipe>())).thenAnswer { it.arguments[0] as Recipe }

        val results = spoonacularService.searchRecipes("sushi", 10)

        assertEquals(1, results.size)
        assertEquals("Sushi Roll", results[0].title)
        verify(restTemplate, times(1)).getForObject(any<String>(), eq(SpoonacularSearchResult::class.java))
    }

    @Test
    fun `searchRecipes throws SpoonacularApiException on API failure`() {
        whenever(recipeRepository.findByTitleContainingIgnoreCase("pizza")).thenReturn(emptyList())
        whenever(restTemplate.getForObject(any<String>(), eq(SpoonacularSearchResult::class.java)))
            .thenThrow(RestClientException("API error"))

        assertThrows(SpoonacularApiException::class.java) {
            spoonacularService.searchRecipes("pizza", 10)
        }
    }

    @Test
    fun `getRecipeById returns cached recipe with complete data`() {
        val cached = Recipe(spoonacularId = 5L, title = "Cached Dish", summary = "Full summary")
        whenever(recipeRepository.findBySpoonacularId(5L)).thenReturn(cached)

        val result = spoonacularService.getRecipeById(5L)

        assertNotNull(result)
        assertEquals("Cached Dish", result.title)
        verify(restTemplate, never()).getForObject(any<String>(), any<Class<*>>())
    }

    @Test
    fun `getRecipeById calls API when not cached`() {
        whenever(recipeRepository.findBySpoonacularId(10L)).thenReturn(null)

        val apiResponse = SpoonacularRecipeResponse(
            id = 10L,
            title = "Fetched Dish",
            image = "img.jpg",
            summary = "API summary",
            cuisines = listOf("Italian"),
        )
        whenever(restTemplate.getForObject(any<String>(), eq(SpoonacularRecipeResponse::class.java))).thenReturn(apiResponse)
        whenever(recipeRepository.save(any<Recipe>())).thenAnswer { it.arguments[0] as Recipe }

        val result = spoonacularService.getRecipeById(10L)

        assertNotNull(result)
        assertEquals("Fetched Dish", result.title)
        assertEquals(listOf("Italian"), result.cuisines)
        verify(restTemplate, times(1)).getForObject(any<String>(), eq(SpoonacularRecipeResponse::class.java))
    }

    @Test
    fun `findByIngredients returns results from API`() {
        val apiResults = arrayOf(
            SpoonacularFindByIngredientsResult(
                id = 200L, title = "Tomato Basil Pasta", image = "img.jpg", imageType = "jpg"
            )
        )
        @Suppress("UNCHECKED_CAST")
        val arrayClass = Array<SpoonacularFindByIngredientsResult>::class.java
        whenever(restTemplate.getForObject(any<String>(), eq(arrayClass))).thenReturn(apiResults)
        whenever(recipeRepository.findBySpoonacularId(200L)).thenReturn(null)
        whenever(recipeRepository.save(any<Recipe>())).thenAnswer { it.arguments[0] as Recipe }

        val results = spoonacularService.findByIngredients(listOf("tomato", "basil"))

        assertEquals(1, results.size)
        assertEquals("Tomato Basil Pasta", results[0].title)
    }

    @Test
    fun `findByIngredients throws SpoonacularApiException on API failure`() {
        @Suppress("UNCHECKED_CAST")
        val arrayClass = Array<SpoonacularFindByIngredientsResult>::class.java
        whenever(restTemplate.getForObject(any<String>(), eq(arrayClass)))
            .thenThrow(RestClientException("API error"))

        assertThrows(SpoonacularApiException::class.java) {
            spoonacularService.findByIngredients(listOf("tomato"))
        }
    }

    @Test
    fun `getCachedRecipes returns all recipes from DB`() {
        val recipes = listOf(
            Recipe(spoonacularId = 1L, title = "Recipe 1"),
            Recipe(spoonacularId = 2L, title = "Recipe 2"),
        )
        whenever(recipeRepository.findAll()).thenReturn(recipes)

        val results = spoonacularService.getCachedRecipes()

        assertEquals(2, results.size)
        assertEquals("Recipe 1", results[0].title)
        assertEquals("Recipe 2", results[1].title)
    }
}