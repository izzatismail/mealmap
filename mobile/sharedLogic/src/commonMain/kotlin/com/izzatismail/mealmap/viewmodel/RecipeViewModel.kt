package com.izzatismail.mealmap.viewmodel

import com.izzatismail.mealmap.model.Recipe
import com.izzatismail.mealmap.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
)

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class RecipeViewModel(
    private val repository: RecipeRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    constructor(repository: RecipeRepository) : this(repository, CoroutineScope(SupervisorJob() + Dispatchers.Main))
    private val _listState = MutableStateFlow(RecipeListUiState())
    val listState: StateFlow<RecipeListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(RecipeDetailUiState())
    val detailState: StateFlow<RecipeDetailUiState> = _detailState.asStateFlow()

    fun clear() {
        scope.cancel()
    }

    fun search(query: String) {
        if (query.isBlank()) return
        _listState.value = _listState.value.copy(isLoading = true, error = null, query = query)
        scope.launch {
            try {
                val recipes = repository.searchRecipes(query)
                _listState.value = _listState.value.copy(
                    recipes = recipes, isLoading = false
                )
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    isLoading = false, error = e.message ?: "Search failed"
                )
            }
        }
    }

    fun onQueryChanged(query: String) {
        _listState.value = _listState.value.copy(query = query)
    }

    fun loadCachedRecipes() {
        _listState.value = _listState.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val recipes = repository.getCachedRecipes()
                _listState.value = _listState.value.copy(
                    recipes = recipes, isLoading = false
                )
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    isLoading = false, error = e.message ?: "Failed to load cached recipes"
                )
            }
        }
    }

    fun loadRecipeDetail(id: Long) {
        _detailState.value = RecipeDetailUiState(isLoading = true)
        scope.launch {
            try {
                val recipe = repository.getRecipeById(id)
                _detailState.value = RecipeDetailUiState(recipe = recipe)
            } catch (e: Exception) {
                _detailState.value = RecipeDetailUiState(
                    error = e.message ?: "Failed to load recipe"
                )
            }
        }
    }
}