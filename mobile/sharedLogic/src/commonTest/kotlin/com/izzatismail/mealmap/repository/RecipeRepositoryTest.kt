package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.api.ApiConfig
import com.izzatismail.mealmap.api.MealMapApi
import com.izzatismail.mealmap.model.Recipe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RecipeRepositoryTest {

    private fun mockClient(engine: MockEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    private fun apiWithJsonResponse(json: String, status: HttpStatusCode = HttpStatusCode.OK): MealMapApi {
        val engine = MockEngine {
            respond(
                content = ByteReadChannel(json),
                status = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        return MealMapApi(mockClient(engine))
    }

    @BeforeTest
    fun setup() {
        ApiConfig.baseUrl = "http://test.local"
    }

    @Test
    fun `searchRecipes returns recipes from API when no database`() = runTest {
        val repo = RecipeRepository(apiWithJsonResponse("""
            [{"spoonacularId":1,"title":"Pasta","image":"img.jpg","readyInMinutes":20,"servings":2}]
        """), null)

        val results = repo.searchRecipes("pasta")
        assertEquals(1, results.size)
        assertEquals("Pasta", results.first().title)
    }

    @Test
    fun `searchRecipes handles empty results`() = runTest {
        val repo = RecipeRepository(apiWithJsonResponse("[]"), null)

        val results = repo.searchRecipes("nothing")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `searchRecipes throws when API fails with no database`() = runTest {
        val repo = RecipeRepository(
            apiWithJsonResponse("Not Found", HttpStatusCode.NotFound),
            null
        )

        assertFailsWith<Exception> {
            repo.searchRecipes("fail")
        }
    }

    @Test
    fun `getRecipeById returns recipe from API`() = runTest {
        val repo = RecipeRepository(apiWithJsonResponse("""
            {"spoonacularId":42,"title":"Test Recipe","image":"img.jpg","readyInMinutes":30,"servings":4}
        """), null)

        val recipe = repo.getRecipeById(42)
        assertEquals(42L, recipe.spoonacularId)
        assertEquals("Test Recipe", recipe.title)
    }

    @Test
    fun `getRecipeById throws when API fails with no database`() = runTest {
        val repo = RecipeRepository(
            apiWithJsonResponse("Not Found", HttpStatusCode.NotFound),
            null
        )

        assertFailsWith<Exception> {
            repo.getRecipeById(999)
        }
    }

    @Test
    fun `getCachedRecipes returns empty list when no database`() = runTest {
        val repo = RecipeRepository(null, null)

        val results = repo.getCachedRecipes()
        assertTrue(results.isEmpty())
    }

    @Test
    fun `searchRecipes passes limit parameter to API`() = runTest {
        val engine = MockEngine { request ->
            assertEquals("5", request.url.parameters["limit"])
            respond(
                content = ByteReadChannel("[]"),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = MealMapApi(mockClient(engine))
        val repo = RecipeRepository(api, null)

        val results = repo.searchRecipes("pasta", 5)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `searchRecipes API failure error message is preserved`() = runTest {
        val repo = RecipeRepository(
            apiWithJsonResponse("Server Error", HttpStatusCode.InternalServerError),
            null
        )

        val error = assertFailsWith<Exception> {
            repo.searchRecipes("fail")
        }
        assertTrue(error.message?.isNotEmpty() == true)
    }

    @Test
    fun `getRecipeById returns correct recipe`() = runTest {
        val repo = RecipeRepository(apiWithJsonResponse("""
            {"spoonacularId":7,"title":"Specific Dish","image":"img.jpg"}
        """), null)

        val recipe = repo.getRecipeById(7)
        assertEquals(7L, recipe.spoonacularId)
        assertEquals("Specific Dish", recipe.title)
    }

    @Test
    fun `searchRecipes preserves recipe fields returned by API`() = runTest {
        val repo = RecipeRepository(apiWithJsonResponse("""
            [{"spoonacularId":5,"title":"Full Recipe","image":"full.jpg",
              "readyInMinutes":45,"servings":6,"healthScore":80.0,
              "sourceUrl":"http://example.com","vegetarian":true}]
        """), null)

        val results = repo.searchRecipes("test")
        val recipe = results.first()
        assertEquals(5L, recipe.spoonacularId)
        assertEquals("Full Recipe", recipe.title)
        assertEquals("full.jpg", recipe.image)
        assertEquals(45, recipe.readyInMinutes)
        assertEquals(6, recipe.servings)
        assertEquals(80.0, recipe.healthScore)
        assertEquals(true, recipe.vegetarian)
    }
}