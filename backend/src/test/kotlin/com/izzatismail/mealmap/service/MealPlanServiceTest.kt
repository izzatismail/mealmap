package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.dto.AddPlannedMealRequest
import com.izzatismail.mealmap.entity.MealPlan
import com.izzatismail.mealmap.entity.MealType
import com.izzatismail.mealmap.entity.PlannedMeal
import com.izzatismail.mealmap.entity.Recipe
import com.izzatismail.mealmap.entity.User
import com.izzatismail.mealmap.exception.ResourceNotFoundException
import com.izzatismail.mealmap.repository.MealPlanRepository
import com.izzatismail.mealmap.repository.RecipeRepository
import com.izzatismail.mealmap.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@SpringBootTest
@ActiveProfiles("test")
class MealPlanServiceTest {

    @Autowired
    private lateinit var mealPlanService: MealPlanService

    @MockBean
    private lateinit var mealPlanRepository: MealPlanRepository

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var recipeRepository: RecipeRepository

    private val userId = 1L
    private val planId = 1L
    private val recipeId = 100L

    private fun createUser() = User(id = userId, email = "test@example.com", password = "hashed")
    private fun createRecipe() = Recipe(id = recipeId, spoonacularId = 999L, title = "Test Recipe")
    private fun createMealPlan(user: User = createUser()) = MealPlan(
        id = planId, user = user,
        weekStart = LocalDate.now(), weekEnd = LocalDate.now().plusDays(7),
        createdAt = LocalDateTime.now(),
    )

    @Test
    fun `addMealToPlan adds a planned meal and returns updated plan`() {
        val user = createUser()
        val plan = createMealPlan(user)
        val recipe = createRecipe()

        whenever(mealPlanRepository.findByIdWithPlannedMeals(planId)).thenReturn(plan)
        whenever(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe))
        whenever(mealPlanRepository.save(any<MealPlan>())).thenAnswer { it.arguments[0] as MealPlan }

        val request = AddPlannedMealRequest(recipeId = recipeId, mealType = "DINNER", dayOfWeek = 1, servings = 2)
        val result = mealPlanService.addMealToPlan(userId, planId, request)

        assertNotNull(result)
        assertEquals(1, result.plannedMeals.size)
        assertEquals("Test Recipe", result.plannedMeals.first().recipeTitle)
        verify(mealPlanRepository).save(any<MealPlan>())
    }

    @Test
    fun `addMealToPlan throws when meal plan not found`() {
        whenever(mealPlanRepository.findByIdWithPlannedMeals(planId)).thenReturn(null)

        val request = AddPlannedMealRequest(recipeId = recipeId, mealType = "LUNCH", dayOfWeek = 2, servings = 1)
        val exception = assertThrows<ResourceNotFoundException> {
            mealPlanService.addMealToPlan(userId, planId, request)
        }
        assertTrue(exception.message!!.contains("not found"))
    }

    @Test
    fun `addMealToPlan throws when user does not own plan`() {
        val otherUser = User(id = 999L, email = "other@example.com", password = "hashed")
        val plan = createMealPlan(otherUser)

        whenever(mealPlanRepository.findByIdWithPlannedMeals(planId)).thenReturn(plan)

        val request = AddPlannedMealRequest(recipeId = recipeId, mealType = "LUNCH", dayOfWeek = 2, servings = 1)
        val exception = assertThrows<ResourceNotFoundException> {
            mealPlanService.addMealToPlan(userId, planId, request)
        }
        assertTrue(exception.message!!.contains("not found"))
    }

    @Test
    fun `addMealToPlan throws when recipe not found`() {
        val user = createUser()
        val plan = createMealPlan(user)

        whenever(mealPlanRepository.findByIdWithPlannedMeals(planId)).thenReturn(plan)
        whenever(recipeRepository.findById(recipeId)).thenReturn(Optional.empty())

        val request = AddPlannedMealRequest(recipeId = recipeId, mealType = "DINNER", dayOfWeek = 1, servings = 2)
        val exception = assertThrows<ResourceNotFoundException> {
            mealPlanService.addMealToPlan(userId, planId, request)
        }
        assertTrue(exception.message!!.contains("not found"))
    }

    @Test
    fun `removeMealFromPlan removes a planned meal and returns updated plan`() {
        val user = createUser()
        val plan = createMealPlan(user)
        val recipe = createRecipe()
        val meal = PlannedMeal(id = 10L, mealPlan = plan, recipe = recipe, mealType = MealType.DINNER, dayOfWeek = 1, servings = 2)
        plan.plannedMeals.add(meal)

        whenever(mealPlanRepository.findByIdWithPlannedMeals(planId)).thenReturn(plan)
        whenever(mealPlanRepository.save(any<MealPlan>())).thenAnswer { it.arguments[0] as MealPlan }

        val result = mealPlanService.removeMealFromPlan(userId, planId, 10L)

        assertNotNull(result)
        assertTrue(result.plannedMeals.isEmpty())
        verify(mealPlanRepository).save(any<MealPlan>())
    }

    @Test
    fun `removeMealFromPlan throws when planned meal not found`() {
        val user = createUser()
        val plan = createMealPlan(user)

        whenever(mealPlanRepository.findByIdWithPlannedMeals(planId)).thenReturn(plan)

        val exception = assertThrows<ResourceNotFoundException> {
            mealPlanService.removeMealFromPlan(userId, planId, 999L)
        }
        assertTrue(exception.message!!.contains("not found"))
    }
}
