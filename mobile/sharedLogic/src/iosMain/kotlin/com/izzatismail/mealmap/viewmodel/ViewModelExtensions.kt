package com.izzatismail.mealmap.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val iosScope = CoroutineScope(Dispatchers.Main)

fun AuthViewModel.checkAuthAsync(onResult: (AuthUiState) -> Unit) {
    checkAuth()
    iosScope.launch {
        val result = state.first { !it.isCheckingAuth }
        onResult(result)
    }
}

fun AuthViewModel.loginAsync(email: String, password: String, onResult: (AuthUiState) -> Unit) {
    login(email, password)
    iosScope.launch {
        val result = state.first { !it.isLoading }
        onResult(result)
    }
}

fun AuthViewModel.registerAsync(email: String, password: String, name: String, onResult: (AuthUiState) -> Unit) {
    register(email, password, name)
    iosScope.launch {
        val result = state.first { !it.isLoading }
        onResult(result)
    }
}

fun FavoriteViewModel.loadFavoritesAsync(onResult: (FavoriteUiState) -> Unit) {
    loadFavorites()
    iosScope.launch {
        val result = state.first { !it.isLoading }
        onResult(result)
    }
}

fun MealPlanViewModel.loadMealPlansAsync(onResult: (MealPlanUiState) -> Unit) {
    loadMealPlans()
    iosScope.launch {
        val result = state.first { !it.isLoading }
        onResult(result)
    }
}

fun ShoppingListViewModel.loadCurrentListAsync(onResult: (ShoppingListUiState) -> Unit) {
    loadCurrentList()
    iosScope.launch {
        val result = state.first { !it.isLoading }
        onResult(result)
    }
}

fun PantryViewModel.loadItemsAsync(onResult: (PantryUiState) -> Unit) {
    loadItems()
    iosScope.launch {
        val result = state.first { !it.isLoading }
        onResult(result)
    }
}