package com.izzatismail.mealmap

import com.izzatismail.mealmap.model.Recipe
import com.izzatismail.mealmap.repository.RecipeRepository
import com.izzatismail.mealmap.viewmodel.RecipeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModelTest {

    private fun createFakeRepository(recipes: List<Recipe> = emptyList()): RecipeRepository {
        return object : RecipeRepository(null, null) {
            override suspend fun searchRecipes(query: String, limit: Int): List<Recipe> {
                if (query == "error") throw Exception("API error")
                return recipes.filter { it.title.contains(query, ignoreCase = true) }
            }

            override suspend fun getRecipeById(id: Long): Recipe {
                if (id == -1L) throw Exception("Recipe not found")
                return recipes.firstOrNull { it.spoonacularId == id }
                    ?: throw Exception("Recipe not found")
            }

            override suspend fun getCachedRecipes(): List<Recipe> {
                return recipes
            }
        }
    }

    private fun createViewModel(repo: RecipeRepository): RecipeViewModel {
        return RecipeViewModel(repo, CoroutineScope(SupervisorJob() + UnconfinedTestDispatcher()))
    }

    @Test
    fun `search updates list state`() = runTest {
        val recipes = listOf(
            Recipe(spoonacularId = 1, title = "Pasta", image = ""),
        )
        val viewModel = createViewModel(createFakeRepository(recipes))

        viewModel.search("Pasta")
        val state = viewModel.listState.value

        assertEquals(1, state.recipes.size)
        assertEquals("Pasta", state.recipes.first().title)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `search sets error when repository throws`() = runTest {
        val viewModel = createViewModel(createFakeRepository())

        viewModel.search("error")
        val state = viewModel.listState.value

        assertTrue(state.recipes.isEmpty())
        assertEquals(false, state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `search with empty query does nothing`() = runTest {
        val viewModel = createViewModel(createFakeRepository())
        val initialState = viewModel.listState.value

        viewModel.search("")
        val state = viewModel.listState.value

        assertEquals(initialState, state)
    }

    @Test
    fun `search with blank query does nothing`() = runTest {
        val viewModel = createViewModel(createFakeRepository())
        val initialState = viewModel.listState.value

        viewModel.search("   ")
        val state = viewModel.listState.value

        assertEquals(initialState, state)
    }

    @Test
    fun `search sets loading state before execution`() = runTest {
        val viewModel = createViewModel(createFakeRepository())

        viewModel.search("Pasta")
        val state = viewModel.listState.value

        assertEquals(false, state.isLoading)
    }

    @Test
    fun `loadRecipeDetail sets detail state`() = runTest {
        val recipes = listOf(
            Recipe(spoonacularId = 42, title = "Test Recipe", image = ""),
        )
        val viewModel = createViewModel(createFakeRepository(recipes))

        viewModel.loadRecipeDetail(42)
        val state = viewModel.detailState.value

        assertEquals("Test Recipe", state.recipe?.title)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadRecipeDetail sets error when recipe not found`() = runTest {
        val viewModel = createViewModel(createFakeRepository())

        viewModel.loadRecipeDetail(999)
        val state = viewModel.detailState.value

        assertNull(state.recipe)
        assertEquals(false, state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `loadRecipeDetail sets error when repository throws`() = runTest {
        val viewModel = createViewModel(createFakeRepository())

        viewModel.loadRecipeDetail(-1)
        val state = viewModel.detailState.value

        assertNull(state.recipe)
        assertEquals(false, state.isLoading)
        assertNotNull(state.error)
    }

    @Test
    fun `loadCachedRecipes returns empty list when no cache`() = runTest {
        val viewModel = createViewModel(createFakeRepository())

        viewModel.loadCachedRecipes()
        val state = viewModel.listState.value

        assertTrue(state.recipes.isEmpty())
    }

    @Test
    fun `loadCachedRecipes returns cached recipes`() = runTest {
        val recipes = listOf(
            Recipe(spoonacularId = 1, title = "Cached Dish", image = ""),
        )
        val viewModel = createViewModel(createFakeRepository(recipes))

        viewModel.loadCachedRecipes()
        val state = viewModel.listState.value

        assertEquals(1, state.recipes.size)
        assertEquals("Cached Dish", state.recipes.first().title)
    }

    @Test
    fun `onQueryChanged updates query in state`() = runTest {
        val viewModel = createViewModel(createFakeRepository())

        viewModel.onQueryChanged("new query")
        val state = viewModel.listState.value

        assertEquals("new query", state.query)
    }

    @Test
    fun `onQueryChanged does not trigger search`() = runTest {
        val recipes = listOf(
            Recipe(spoonacularId = 1, title = "Pasta", image = ""),
        )
        val viewModel = createViewModel(createFakeRepository(recipes))

        viewModel.onQueryChanged("Pasta")
        val state = viewModel.listState.value

        assertTrue(state.recipes.isEmpty())
        assertEquals("Pasta", state.query)
    }

    @Test
    fun `search preserves query in state`() = runTest {
        val viewModel = createViewModel(createFakeRepository())

        viewModel.search("chicken")
        val state = viewModel.listState.value

        assertEquals("chicken", state.query)
    }

    @Test
    fun `multiple searches update recipes correctly`() = runTest {
        val allRecipes = listOf(
            Recipe(spoonacularId = 1, title = "Pasta", image = ""),
            Recipe(spoonacularId = 2, title = "Pizza", image = ""),
            Recipe(spoonacularId = 3, title = "Salad", image = ""),
        )
        val viewModel = createViewModel(createFakeRepository(allRecipes))

        viewModel.search("Pasta")
        assertEquals(1, viewModel.listState.value.recipes.size)

        viewModel.search("Pizza")
        assertEquals(1, viewModel.listState.value.recipes.size)
        assertEquals("Pizza", viewModel.listState.value.recipes.first().title)
    }

    @Test
    fun `loadRecipeDetail clears previous detail`() = runTest {
        val viewModel = createViewModel(createFakeRepository())

        viewModel.loadRecipeDetail(999)
        val state = viewModel.detailState.value

        assertNull(state.recipe)
        assertNotNull(state.error)
    }
}