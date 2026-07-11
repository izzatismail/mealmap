package com.izzatismail.mealmap.viewmodel

import com.izzatismail.mealmap.api.ShoppingListApi
import com.izzatismail.mealmap.model.ShoppingListDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShoppingListUiState(
    val shoppingList: ShoppingListDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ShoppingListViewModel(
    private val shoppingListApi: ShoppingListApi,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    constructor(shoppingListApi: ShoppingListApi) : this(shoppingListApi, CoroutineScope(SupervisorJob() + Dispatchers.Main))

    private val _state = MutableStateFlow(ShoppingListUiState())
    val state: StateFlow<ShoppingListUiState> = _state.asStateFlow()

    fun loadCurrentList() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val list = shoppingListApi.getCurrentShoppingList()
                _state.value = ShoppingListUiState(shoppingList = list, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load shopping list",
                )
            }
        }
    }

    fun generateFromMealPlan(mealPlanId: Long) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val list = shoppingListApi.generateShoppingList(mealPlanId)
                _state.value = ShoppingListUiState(shoppingList = list, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to generate shopping list",
                )
            }
        }
    }

    fun toggleItem(itemId: Long) {
        scope.launch {
            try {
                val updated = shoppingListApi.toggleItem(itemId)
                val current = _state.value.shoppingList ?: return@launch
                _state.value = _state.value.copy(
                    shoppingList = current.copy(
                        items = current.items.map { if (it.id == itemId) updated else it }
                    )
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to toggle item",
                )
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}