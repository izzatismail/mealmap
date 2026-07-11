package com.izzatismail.mealmap.viewmodel

import com.izzatismail.mealmap.api.PantryApi
import com.izzatismail.mealmap.api.PantryItemUpdateRequest
import com.izzatismail.mealmap.model.PantryItemDto
import com.izzatismail.mealmap.model.PantryItemRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PantryUiState(
    val items: List<PantryItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class PantryViewModel(
    private val pantryApi: PantryApi,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    constructor(pantryApi: PantryApi) : this(pantryApi, CoroutineScope(SupervisorJob() + Dispatchers.Main))

    private val _state = MutableStateFlow(PantryUiState())
    val state: StateFlow<PantryUiState> = _state.asStateFlow()

    fun loadItems() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val items = pantryApi.getPantryItems()
                _state.value = PantryUiState(items = items, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load pantry items",
                )
            }
        }
    }

    fun addItem(name: String, amount: Double, unit: String, expirationDate: String? = null) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val item = pantryApi.addPantryItem(
                    PantryItemRequest(name = name, amount = amount, unit = unit, expirationDate = expirationDate)
                )
                _state.value = _state.value.copy(
                    items = _state.value.items + item,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add item",
                )
            }
        }
    }

    fun updateItem(id: Long, amount: Double, unit: String) {
        scope.launch {
            try {
                val updated = pantryApi.updatePantryItem(id, PantryItemUpdateRequest(amount = amount, unit = unit))
                _state.value = _state.value.copy(
                    items = _state.value.items.map { if (it.id == id) updated else it }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to update item",
                )
            }
        }
    }

    fun deleteItem(id: Long) {
        scope.launch {
            try {
                pantryApi.deletePantryItem(id)
                _state.value = _state.value.copy(
                    items = _state.value.items.filter { it.id != id }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to delete item",
                )
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}