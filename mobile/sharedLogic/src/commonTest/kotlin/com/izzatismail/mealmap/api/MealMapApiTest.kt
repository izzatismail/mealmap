package com.izzatismail.mealmap.api

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

class MealMapApiTest {

    private fun mockClient(engine: MockEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @BeforeTest
    fun setup() {
        ApiConfig.baseUrl = "http://test.local"
    }

    @Test
    fun `searchRecipes returns recipes from API`() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/api/recipes/search", request.url.encodedPath)
            assertEquals("pasta", request.url.parameters["query"])
            assertEquals("10", request.url.parameters["limit"])
            respond(
                content = ByteReadChannel("""
                    [{"spoonacularId":1,"title":"Pasta","image":"img.jpg","readyInMinutes":20,"servings":2}]
                """),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = MealMapApi(mockClient(engine))
        val results = api.searchRecipes("pasta")
        assertEquals(1, results.size)
        assertEquals("Pasta", results.first().title)
        assertEquals(1L, results.first().spoonacularId)
    }

    @Test
    fun `searchRecipes with limit passes parameter`() = runTest {
        val engine = MockEngine { request ->
            assertEquals("5", request.url.parameters["limit"])
            respond(
                content = ByteReadChannel("[]"),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = MealMapApi(mockClient(engine))
        val results = api.searchRecipes("pasta", 5)
        assertTrue(results.isEmpty())
    }

    @Test
    fun `getRecipeById returns single recipe`() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/api/recipes/42", request.url.encodedPath)
            respond(
                content = ByteReadChannel("""
                    {"spoonacularId":42,"title":"Test Recipe","image":"img.jpg","readyInMinutes":30,"servings":4}
                """),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = MealMapApi(mockClient(engine))
        val recipe = api.getRecipeById(42)
        assertEquals(42L, recipe.spoonacularId)
        assertEquals("Test Recipe", recipe.title)
    }

    @Test
    fun `findByIngredients returns matching recipes`() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/api/recipes/by-ingredients", request.url.encodedPath)
            respond(
                content = ByteReadChannel("""
                    [{"spoonacularId":10,"title":"Chicken Rice","image":"img.jpg"}]
                """),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = MealMapApi(mockClient(engine))
        val results = api.findByIngredients(listOf("chicken", "rice"))
        assertEquals(1, results.size)
        assertEquals("Chicken Rice", results.first().title)
    }

    @Test
    fun `getCachedRecipes hits correct endpoint`() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/api/recipes/cached", request.url.encodedPath)
            respond(
                content = ByteReadChannel("[]"),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = MealMapApi(mockClient(engine))
        val results = api.getCachedRecipes()
        assertTrue(results.isEmpty())
    }

    @Test
    fun `API error throws exception`() = runTest {
        val engine = MockEngine {
            respond(
                content = ByteReadChannel("Not Found"),
                status = HttpStatusCode.NotFound
            )
        }
        val api = MealMapApi(mockClient(engine))
        assertFailsWith<Exception> {
            api.searchRecipes("fail")
        }
    }

    @Test
    fun `malformed JSON throws exception`() = runTest {
        val engine = MockEngine {
            respond(
                content = ByteReadChannel("invalid json"),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val api = MealMapApi(mockClient(engine))
        assertFailsWith<Exception> {
            api.searchRecipes("test")
        }
    }
}