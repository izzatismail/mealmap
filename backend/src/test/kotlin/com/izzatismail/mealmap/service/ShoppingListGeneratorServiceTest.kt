package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.dto.SpoonacularIngredient
import com.izzatismail.mealmap.entity.MealPlan
import com.izzatismail.mealmap.entity.MealType
import com.izzatismail.mealmap.entity.PantryItem
import com.izzatismail.mealmap.entity.PlannedMeal
import com.izzatismail.mealmap.entity.Recipe
import com.izzatismail.mealmap.entity.ShoppingItem
import com.izzatismail.mealmap.entity.ShoppingList
import com.izzatismail.mealmap.entity.User
import com.izzatismail.mealmap.repository.MealPlanRepository
import com.izzatismail.mealmap.repository.PantryItemRepository
import com.izzatismail.mealmap.repository.ShoppingItemRepository
import com.izzatismail.mealmap.repository.ShoppingListRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.Optional

@SpringBootTest
@ActiveProfiles("test")
class ShoppingListGeneratorServiceTest {

    @Autowired
    private lateinit var generatorService: ShoppingListGeneratorService

    @MockBean
    private lateinit var mealPlanRepository: MealPlanRepository

    @MockBean
    private lateinit var shoppingListRepository: ShoppingListRepository

    @MockBean
    private lateinit var shoppingItemRepository: ShoppingItemRepository

    @MockBean
    private lateinit var pantryItemRepository: PantryItemRepository

    private val objectMapper = ObjectMapper()
    private val userId = 1L
    private val mealPlanId = 1L

    @Test
    fun `generateShoppingList creates items from planned meals`() {
        val user = User(id = userId, email = "[EMAIL]", password = "hashed")
        val recipe = Recipe(
            id = 1L,
            spoonacularId = 100L,
            title = "Pasta Carbonara",
            servings = 2,
            ingredients = objectMapper.writeValueAsString(
                listOf(
                    SpoonacularIngredient(name = "Pasta", amount = 200.0, unit = "g", aisle = "Pasta"),
                    SpoonacularIngredient(name = "Egg", amount = 2.0, unit = "pieces", aisle = "Dairy"),
                )
            ),
        )
        val mealPlan = MealPlan(
            id = mealPlanId,
            user = user,
            weekStart = LocalDate.now(),
            weekEnd = LocalDate.now().plusDays(7),
        )
        val plannedMeal = PlannedMeal(
            id = 1L,
            mealPlan = mealPlan,
            recipe = recipe,
            mealType = MealType.DINNER,
            dayOfWeek = 1,
            servings = 4,
        )
        mealPlan.plannedMeals.add(plannedMeal)

        whenever(mealPlanRepository.findByIdWithPlannedMeals(mealPlanId)).thenReturn(mealPlan)
        whenever(shoppingListRepository.findByMealPlanId(mealPlanId)).thenReturn(null)
        whenever(shoppingListRepository.save(any<ShoppingList>())).thenAnswer { it.arguments[0] as ShoppingList }

        val result = generatorService.generateShoppingList(userId, mealPlanId)

        assertNotNull(result)
        assertEquals(2, result.items.size)
        assertEquals(400.0, result.items.find { it.name == "Pasta" }?.amount)
        assertEquals(4.0, result.items.find { it.name == "Egg" }?.amount)
        verify(shoppingListRepository).save(any<ShoppingList>())
    }

    @Test
    fun `generateShoppingList returns existing list when already generated`() {
        val user = User(id = userId, email = "[EMAIL]", password = "hashed")
        val mealPlan = MealPlan(id = mealPlanId, user = user, weekStart = LocalDate.now(), weekEnd = LocalDate.now().plusDays(7))
        val existing = ShoppingList(id = 1L, mealPlan = mealPlan, user = user)

        whenever(mealPlanRepository.findByIdWithPlannedMeals(mealPlanId)).thenReturn(mealPlan)
        whenever(shoppingListRepository.findByMealPlanId(mealPlanId)).thenReturn(existing)

        val result = generatorService.generateShoppingList(userId, mealPlanId)

        assertNotNull(result)
        assertEquals(1L, result.id)
        verify(shoppingListRepository, never()).save(any())
    }

    @Test
    fun `generateShoppingList with regenerate removes old and creates new`() {
        val user = User(id = userId, email = "[EMAIL]", password = "hashed")
        val recipe = Recipe(
            id = 1L, spoonacularId = 100L, title = "Pasta", servings = 2,
            ingredients = objectMapper.writeValueAsString(
                listOf(SpoonacularIngredient(name = "Pasta", amount = 200.0, unit = "g", aisle = "Pasta"))
            ),
        )
        val mealPlan = MealPlan(id = mealPlanId, user = user, weekStart = LocalDate.now(), weekEnd = LocalDate.now().plusDays(7))
        mealPlan.plannedMeals.add(PlannedMeal(mealPlan = mealPlan, recipe = recipe, mealType = MealType.DINNER, dayOfWeek = 1, servings = 2))
        val existing = ShoppingList(id = 1L, mealPlan = mealPlan, user = user)

        whenever(mealPlanRepository.findByIdWithPlannedMeals(mealPlanId)).thenReturn(mealPlan)
        whenever(shoppingListRepository.findByMealPlanId(mealPlanId)).thenReturn(existing)
        whenever(shoppingListRepository.save(any<ShoppingList>())).thenAnswer { it.arguments[0] as ShoppingList }

        val result = generatorService.generateShoppingList(userId, mealPlanId, regenerate = true)

        assertNotNull(result)
        assertEquals(1, result.items.size)
        verify(shoppingListRepository).delete(existing)
    }

    @Test
    fun `generateShoppingList aggregates duplicate ingredients`() {
        val user = User(id = userId, email = "[EMAIL]", password = "hashed")
        val recipe1 = Recipe(
            id = 1L, spoonacularId = 100L, title = "Pasta Carbonara", servings = 2,
            ingredients = objectMapper.writeValueAsString(
                listOf(SpoonacularIngredient(name = "Pasta", amount = 200.0, unit = "g", aisle = "Pasta"))
            ),
        )
        val recipe2 = Recipe(
            id = 2L, spoonacularId = 101L, title = "Pasta Bolognese", servings = 2,
            ingredients = objectMapper.writeValueAsString(
                listOf(SpoonacularIngredient(name = "Pasta", amount = 150.0, unit = "g", aisle = "Pasta"))
            ),
        )
        val mealPlan = MealPlan(id = mealPlanId, user = user, weekStart = LocalDate.now(), weekEnd = LocalDate.now().plusDays(7))
        mealPlan.plannedMeals.add(PlannedMeal(mealPlan = mealPlan, recipe = recipe1, mealType = MealType.DINNER, dayOfWeek = 1, servings = 2))
        mealPlan.plannedMeals.add(PlannedMeal(mealPlan = mealPlan, recipe = recipe2, mealType = MealType.LUNCH, dayOfWeek = 2, servings = 2))

        whenever(mealPlanRepository.findByIdWithPlannedMeals(mealPlanId)).thenReturn(mealPlan)
        whenever(shoppingListRepository.findByMealPlanId(mealPlanId)).thenReturn(null)
        whenever(shoppingListRepository.save(any<ShoppingList>())).thenAnswer { it.arguments[0] as ShoppingList }

        val result = generatorService.generateShoppingList(userId, mealPlanId)

        assertEquals(1, result.items.size)
        assertEquals(350.0, result.items.first().amount)
    }

    @Test
    fun `generateShoppingList subtracts pantry items`() {
        val user = User(id = userId, email = "[EMAIL]", password = "hashed")
        val recipe = Recipe(
            id = 1L, spoonacularId = 100L, title = "Pasta Carbonara", servings = 2,
            ingredients = objectMapper.writeValueAsString(
                listOf(
                    SpoonacularIngredient(name = "Pasta", amount = 200.0, unit = "g", aisle = "Pasta"),
                    SpoonacularIngredient(name = "Egg", amount = 4.0, unit = "pieces", aisle = "Dairy"),
                )
            ),
        )
        val mealPlan = MealPlan(id = mealPlanId, user = user, weekStart = LocalDate.now(), weekEnd = LocalDate.now().plusDays(7))
        mealPlan.plannedMeals.add(PlannedMeal(mealPlan = mealPlan, recipe = recipe, mealType = MealType.DINNER, dayOfWeek = 1, servings = 2))

        val pantryItems = listOf(
            PantryItem(user = user, name = "Pasta", amount = 100.0, unit = "g"),
            PantryItem(user = user, name = "Egg", amount = 4.0, unit = "pieces"),
        )

        whenever(mealPlanRepository.findByIdWithPlannedMeals(mealPlanId)).thenReturn(mealPlan)
        whenever(shoppingListRepository.findByMealPlanId(mealPlanId)).thenReturn(null)
        whenever(pantryItemRepository.findByUserId(userId)).thenReturn(pantryItems)
        whenever(shoppingListRepository.save(any<ShoppingList>())).thenAnswer { it.arguments[0] as ShoppingList }

        val result = generatorService.generateShoppingList(userId, mealPlanId)

        assertEquals(1, result.items.size)
        assertEquals(100.0, result.items.first().amount)
        assertEquals("Pasta", result.items.first().name)
        assertNull(result.items.find { it.name == "Egg" })
    }

    @Test
    fun `generateShoppingList excludes items fully covered by pantry`() {
        val user = User(id = userId, email = "[EMAIL]", password = "hashed")
        val recipe = Recipe(
            id = 1L, spoonacularId = 100L, title = "Omelette", servings = 1,
            ingredients = objectMapper.writeValueAsString(
                listOf(SpoonacularIngredient(name = "Egg", amount = 3.0, unit = "pieces", aisle = "Dairy"))
            ),
        )
        val mealPlan = MealPlan(id = mealPlanId, user = user, weekStart = LocalDate.now(), weekEnd = LocalDate.now().plusDays(7))
        mealPlan.plannedMeals.add(PlannedMeal(mealPlan = mealPlan, recipe = recipe, mealType = MealType.BREAKFAST, dayOfWeek = 1, servings = 1))

        whenever(mealPlanRepository.findByIdWithPlannedMeals(mealPlanId)).thenReturn(mealPlan)
        whenever(shoppingListRepository.findByMealPlanId(mealPlanId)).thenReturn(null)
        whenever(pantryItemRepository.findByUserId(userId)).thenReturn(listOf(PantryItem(user = user, name = "Egg", amount = 6.0, unit = "pieces")))
        whenever(shoppingListRepository.save(any<ShoppingList>())).thenAnswer { it.arguments[0] as ShoppingList }

        val result = generatorService.generateShoppingList(userId, mealPlanId)

        assertTrue(result.items.isEmpty())
    }

    @Test
    fun `toggleItem toggles isChecked state`() {
        val user = User(id = userId, email = "[EMAIL]", password = "hashed")
        val mealPlan = MealPlan(id = mealPlanId, user = user, weekStart = LocalDate.now(), weekEnd = LocalDate.now().plusDays(7))
        val shoppingList = ShoppingList(id = 1L, mealPlan = mealPlan, user = user)
        val item = ShoppingItem(id = 1L, shoppingList = shoppingList, name = "Pasta", amount = 200.0, unit = "g", isChecked = false)

        whenever(shoppingItemRepository.findById(1L)).thenReturn(Optional.of(item))
        whenever(shoppingItemRepository.save(any<ShoppingItem>())).thenAnswer { it.arguments[0] as ShoppingItem }

        val result = generatorService.toggleItem(userId, 1L)

        assertTrue(result.isChecked)

        val result2 = generatorService.toggleItem(userId, 1L)

        assertFalse(result2.isChecked)
    }

    @Test
    fun `toggleItem rejects cross-user access`() {
        val otherUser = User(id = 999L, email = "other@example.com", password = "hashed")
        val mealPlan = MealPlan(id = 2L, user = otherUser, weekStart = LocalDate.now(), weekEnd = LocalDate.now().plusDays(7))
        val shoppingList = ShoppingList(id = 2L, mealPlan = mealPlan, user = otherUser)
        val item = ShoppingItem(id = 2L, shoppingList = shoppingList, name = "Pasta", amount = 200.0, unit = "g")

        whenever(shoppingItemRepository.findById(2L)).thenReturn(Optional.of(item))

        val exception = org.junit.jupiter.api.assertThrows<com.izzatismail.mealmap.exception.ResourceNotFoundException> {
            generatorService.toggleItem(userId, 2L)
        }
        assertTrue(exception.message!!.contains("not found"))
    }
}