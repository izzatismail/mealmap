package com.izzatismail.mealmap.viewmodel

import com.izzatismail.mealmap.api.FavoriteApi
import com.izzatismail.mealmap.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val favorites: List<Recipe> = emptyList(),
    val favoriteIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class FavoriteViewModel(
    private val favoriteApi: FavoriteApi,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    constructor(favoriteApi: FavoriteApi) : this(favoriteApi, CoroutineScope(SupervisorJob() + Dispatchers.Main))

    private val _state = MutableStateFlow(FavoriteUiState())
    val state: StateFlow<FavoriteUiState> = _state.asStateFlow()

    fun loadFavorites() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val favorites = favoriteApi.getFavorites()
                val ids = favoriteApi.getFavoriteIds()
                _state.value = FavoriteUiState(
                    favorites = favorites,
                    favoriteIds = ids.toSet(),
                    isLoading = false,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load favorites",
                )
            }
        }
    }

    fun loadFavoriteIds() {
        scope.launch {
            try {
                val ids = favoriteApi.getFavoriteIds()
                _state.value = _state.value.copy(favoriteIds = ids.toSet())
            } catch (_: Exception) { }
        }
    }

    fun toggleFavorite(recipeId: Long) {
        scope.launch {
            try {
                val isFav = recipeId in _state.value.favoriteIds
                if (isFav) {
                    favoriteApi.removeFavorite(recipeId)
                    _state.value = _state.value.copy(
                        favoriteIds = _state.value.favoriteIds - recipeId,
                        favorites = _state.value.favorites.filter { it.spoonacularId != recipeId },
                    )
                } else {
                    val recipe = favoriteApi.addFavorite(recipeId)
                    _state.value = _state.value.copy(
                        favoriteIds = _state.value.favoriteIds + recipeId,
                        favorites = _state.value.favorites + recipe,
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to update favorite",
                )
            }
        }
    }

    fun isFavorited(recipeId: Long): Boolean = recipeId in _state.value.favoriteIds

    fun clear() {
        scope.cancel()
    }
}
