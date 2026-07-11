package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.dto.MealPlanDto
import com.izzatismail.mealmap.dto.PlannedMealDto
import com.izzatismail.mealmap.dto.CreateMealPlanRequest
import com.izzatismail.mealmap.entity.MealPlan
import com.izzatismail.mealmap.entity.MealType
import com.izzatismail.mealmap.entity.PlannedMeal
import com.izzatismail.mealmap.exception.ResourceNotFoundException
import com.izzatismail.mealmap.repository.MealPlanRepository
import com.izzatismail.mealmap.repository.RecipeRepository
import com.izzatismail.mealmap.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MealPlanService(
    private val mealPlanRepository: MealPlanRepository,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
) {

    fun getMealPlans(userId: Long): List<MealPlanDto> {
        return mealPlanRepository.findByUserIdOrderByWeekStartDesc(userId)
            .map { it.toDto() }
    }

    fun getMealPlanById(userId: Long, id: Long): MealPlanDto {
        val mealPlan = mealPlanRepository.findByIdWithPlannedMeals(id)
            ?: throw ResourceNotFoundException("Meal plan not found with id: $id")
        if (mealPlan.user.id != userId) {
            throw ResourceNotFoundException("Meal plan not found with id: $id")
        }
        return mealPlan.toDto()
    }

    fun createMealPlan(userId: Long, request: CreateMealPlanRequest): MealPlanDto {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        val mealPlan = MealPlan(
            user = user,
            weekStart = request.weekStart,
            weekEnd = request.weekEnd,
        )

        mealPlan.plannedMeals.addAll(request.plannedMeals.map { plannedMealRequest ->
            val recipe = recipeRepository.findById(plannedMealRequest.recipeId)
                .orElseThrow { ResourceNotFoundException("Recipe not found with id: ${plannedMealRequest.recipeId}") }
            PlannedMeal(
                mealPlan = mealPlan,
                recipe = recipe,
                mealType = MealType.valueOf(plannedMealRequest.mealType.uppercase()),
                dayOfWeek = plannedMealRequest.dayOfWeek,
                servings = plannedMealRequest.servings,
            )
        })

        val saved = mealPlanRepository.save(mealPlan)
        return saved.toDto()
    }

    fun deleteMealPlan(userId: Long, id: Long) {
        val mealPlan = mealPlanRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Meal plan not found with id: $id") }
        if (mealPlan.user.id != userId) {
            throw ResourceNotFoundException("Meal plan not found with id: $id")
        }
        mealPlanRepository.deleteById(id)
    }

    private fun MealPlan.toDto() = MealPlanDto(
        id = id,
        userId = user.id,
        weekStart = weekStart,
        weekEnd = weekEnd,
        createdAt = createdAt,
        plannedMeals = plannedMeals.map { plannedMeal ->
            PlannedMealDto(
                id = plannedMeal.id,
                recipeId = plannedMeal.recipe.id,
                recipeTitle = plannedMeal.recipe.title,
                mealType = plannedMeal.mealType,
                dayOfWeek = plannedMeal.dayOfWeek,
                servings = plannedMeal.servings,
            )
        },
    )
}