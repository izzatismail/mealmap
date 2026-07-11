package com.izzatismail.mealmap.viewmodel

import com.izzatismail.mealmap.api.MealPlanApi
import com.izzatismail.mealmap.model.MealPlanDto
import com.izzatismail.mealmap.model.MealPlanRequest
import com.izzatismail.mealmap.model.PlannedMealDto
import com.izzatismail.mealmap.model.PlannedMealRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class MealPlanUiState(
    val mealPlans: List<MealPlanDto> = emptyList(),
    val currentWeekMeals: List<PlannedMealDto> = emptyList(),
    val selectedDay: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class MealPlanViewModel(
    private val mealPlanApi: MealPlanApi,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    constructor(mealPlanApi: MealPlanApi) : this(mealPlanApi, CoroutineScope(SupervisorJob() + Dispatchers.Main))

    private val _state = MutableStateFlow(MealPlanUiState())
    val state: StateFlow<MealPlanUiState> = _state.asStateFlow()

    fun loadMealPlans() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val plans = mealPlanApi.getMealPlans()
                val currentWeekPlan = plans.firstOrNull()
                _state.value = MealPlanUiState(
                    mealPlans = plans,
                    currentWeekMeals = currentWeekPlan?.plannedMeals ?: emptyList(),
                    selectedDay = currentDayOfWeek(),
                    isLoading = false,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load meal plans",
                )
            }
        }
    }

    fun selectDay(day: Int) {
        _state.value = _state.value.copy(selectedDay = day)
    }

    fun addMealToDay(recipeId: Long, mealType: String, dayOfWeek: Int, servings: Int = 1) {
        scope.launch {
            try {
                val existingPlan = _state.value.mealPlans.firstOrNull()
                if (existingPlan != null) {
                    val currentRequests = existingPlan.plannedMeals.map {
                        PlannedMealRequest(it.recipeId, it.mealType, it.dayOfWeek, it.servings)
                    }
                    val updatedMeals = currentRequests + PlannedMealRequest(
                        recipeId = recipeId,
                        mealType = mealType,
                        dayOfWeek = dayOfWeek,
                        servings = servings,
                    )
                    val plan = mealPlanApi.createMealPlan(
                        MealPlanRequest(
                            weekStart = existingPlan.weekStart,
                            weekEnd = existingPlan.weekEnd,
                            plannedMeals = updatedMeals,
                        )
                    )
                    _state.value = _state.value.copy(
                        mealPlans = listOf(plan),
                        currentWeekMeals = plan.plannedMeals,
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to add meal",
                )
            }
        }
    }

    fun removeMeal(plannedMealId: Long) {
        scope.launch {
            try {
                val existingPlan = _state.value.mealPlans.firstOrNull() ?: return@launch
                val updatedMeals = existingPlan.plannedMeals
                    .filter { it.id != plannedMealId }
                    .map { PlannedMealRequest(it.recipeId, it.mealType, it.dayOfWeek, it.servings) }
                val plan = mealPlanApi.createMealPlan(
                    MealPlanRequest(
                        weekStart = existingPlan.weekStart,
                        weekEnd = existingPlan.weekEnd,
                        plannedMeals = updatedMeals,
                    )
                )
                _state.value = _state.value.copy(
                    mealPlans = listOf(plan),
                    currentWeekMeals = plan.plannedMeals,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to remove meal",
                )
            }
        }
    }

    fun mealsForDay(day: Int): List<PlannedMealDto> {
        return _state.value.currentWeekMeals.filter { it.dayOfWeek == day }
    }

    fun clear() {
        scope.cancel()
    }

    companion object {
        fun currentDayOfWeek(): Int {
            val now = Clock.System.now()
            val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
            return localDateTime.dayOfWeek.ordinal
        }
    }
}