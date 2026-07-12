package com.izzatismail.mealmap.viewmodel

import com.izzatismail.mealmap.api.MealPlanApi
import com.izzatismail.mealmap.model.AddPlannedMealRequest
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
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

    private fun currentWeekStart(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val daysFromMonday = now.dayOfWeek.ordinal
        val monday = now.date.minus(daysFromMonday, DateTimeUnit.DAY)
        return monday.toString()
    }

    private fun currentWeekEnd(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val daysFromMonday = now.dayOfWeek.ordinal
        val sunday = now.date.plus(6 - daysFromMonday, DateTimeUnit.DAY)
        return sunday.toString()
    }

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
                    val plan = mealPlanApi.addMeal(
                        planId = existingPlan.id,
                        request = AddPlannedMealRequest(
                            recipeId = recipeId,
                            mealType = mealType,
                            dayOfWeek = dayOfWeek,
                            servings = servings,
                        ),
                    )
                    _state.value = _state.value.copy(
                        mealPlans = listOf(plan) + _state.value.mealPlans.drop(1),
                        currentWeekMeals = plan.plannedMeals,
                    )
                } else {
                    val plan = mealPlanApi.createMealPlan(
                        MealPlanRequest(
                            weekStart = currentWeekStart(),
                            weekEnd = currentWeekEnd(),
                            plannedMeals = listOf(
                                PlannedMealRequest(
                                    recipeId = recipeId,
                                    mealType = mealType,
                                    dayOfWeek = dayOfWeek,
                                    servings = servings,
                                )
                            ),
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
                val plan = mealPlanApi.removeMeal(existingPlan.id, plannedMealId)
                _state.value = _state.value.copy(
                    mealPlans = listOf(plan) + _state.value.mealPlans.drop(1),
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