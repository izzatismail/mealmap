package com.izzatismail.mealmap.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val iosScope = CoroutineScope(Dispatchers.Main)

fun RecipeViewModel.searchAsync(query: String, onResult: (RecipeListUiState) -> Unit) {
    search(query)
    iosScope.launch {
        val result = listState.first { !it.isLoading }
        onResult(result)
    }
}

fun RecipeViewModel.loadDetailAsync(recipeId: Long, onResult: (RecipeDetailUiState) -> Unit) {
    loadRecipeDetail(recipeId)
    iosScope.launch {
        val result = detailState.first { !it.isLoading }
        onResult(result)
    }
}

fun RecipeViewModel.loadCachedAsync(onResult: (RecipeListUiState) -> Unit) {
    loadCachedRecipes()
    iosScope.launch {
        val result = listState.first { !it.isLoading }
        onResult(result)
    }
}