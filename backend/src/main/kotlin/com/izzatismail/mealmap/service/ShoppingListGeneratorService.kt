package com.izzatismail.mealmap.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.izzatismail.mealmap.dto.ShoppingItemDto
import com.izzatismail.mealmap.dto.ShoppingListDto
import com.izzatismail.mealmap.dto.SpoonacularIngredient
import com.izzatismail.mealmap.entity.MealPlan
import com.izzatismail.mealmap.entity.ShoppingItem
import com.izzatismail.mealmap.entity.ShoppingList
import com.izzatismail.mealmap.exception.ResourceNotFoundException
import com.izzatismail.mealmap.repository.MealPlanRepository
import com.izzatismail.mealmap.repository.PantryItemRepository
import com.izzatismail.mealmap.repository.ShoppingListRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ShoppingListGeneratorService(
    private val mealPlanRepository: MealPlanRepository,
    private val shoppingListRepository: ShoppingListRepository,
    private val pantryItemRepository: PantryItemRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = ObjectMapper()

    data class AggregatedIngredient(
        val name: String,
        val amount: Double,
        val unit: String,
        val category: String,
    )

    fun generateShoppingList(mealPlanId: Long, userId: Long? = null): ShoppingListDto {
        val mealPlan = mealPlanRepository.findById(mealPlanId)
            .orElseThrow { ResourceNotFoundException("Meal plan not found with id: $mealPlanId") }

        val existing = shoppingListRepository.findByMealPlanId(mealPlanId)
        if (existing != null) {
            log.info("Returning existing shopping list for meal plan {}", mealPlanId)
            return existing.toDto()
        }

        log.info("Generating shopping list for meal plan {}", mealPlanId)

        val ingredients = extractIngredientsFromMealPlan(mealPlan)

        val aggregated = aggregateIngredients(ingredients)

        val adjusted = if (userId != null) {
            subtractPantryItems(aggregated, userId)
        } else aggregated

        val shoppingList = ShoppingList(
            mealPlan = mealPlan,
            user = mealPlan.user,
        )
        shoppingList.items.addAll(adjusted.map { agg ->
            ShoppingItem(
                shoppingList = shoppingList,
                name = agg.name,
                amount = agg.amount,
                unit = agg.unit,
                category = agg.category,
            )
        })

        val saved = shoppingListRepository.save(shoppingList)
        log.info("Generated shopping list with {} items for meal plan {}", saved.items.size, mealPlanId)
        return saved.toDto()
    }

    private fun extractIngredientsFromMealPlan(mealPlan: MealPlan): List<SpoonacularIngredient> {
        return mealPlan.plannedMeals.flatMap { plannedMeal ->
            val recipe = plannedMeal.recipe
            val scale = plannedMeal.servings.toDouble() / recipe.servings.coerceAtLeast(1)
            parseIngredients(recipe.ingredients).map { ingredient ->
                ingredient.copy(amount = ingredient.amount * scale)
            }
        }
    }

    private fun parseIngredients(json: String): List<SpoonacularIngredient> {
        if (json.isBlank()) return emptyList()
        return try {
            objectMapper.readValue(json, object : TypeReference<List<SpoonacularIngredient>>() {})
        } catch (e: Exception) {
            log.warn("Failed to parse ingredients JSON: {}", e.message)
            emptyList()
        }
    }

    private fun aggregateIngredients(ingredients: List<SpoonacularIngredient>): List<AggregatedIngredient> {
        return ingredients
            .groupBy { it.name.lowercase() to it.unit.lowercase() }
            .map { (_, items) ->
                val first = items.first()
                AggregatedIngredient(
                    name = first.name,
                    amount = items.sumOf { it.amount },
                    unit = first.unit,
                    category = first.aisle,
                )
            }
            .sortedBy { it.category }
    }

    private fun subtractPantryItems(
        aggregated: List<AggregatedIngredient>,
        userId: Long,
    ): List<AggregatedIngredient> {
        val pantryItems = pantryItemRepository.findByUserId(userId)
        if (pantryItems.isEmpty()) return aggregated

        return aggregated.map { agg ->
            val matchingPantry = pantryItems.filter {
                it.name.equals(agg.name, ignoreCase = true) &&
                    it.unit.equals(agg.unit, ignoreCase = true)
            }
            val totalPantryAmount = matchingPantry.sumOf { it.amount }
            val remaining = agg.amount - totalPantryAmount
            if (remaining <= 0.0) null
            else agg.copy(amount = Math.round(remaining * 100.0) / 100.0)
        }.filterNotNull()
    }

    private fun ShoppingList.toDto() = ShoppingListDto(
        id = id,
        mealPlanId = mealPlan.id,
        userId = user.id,
        generatedAt = generatedAt,
        items = items.map { it.toDto() },
    )

    private fun ShoppingItem.toDto() = ShoppingItemDto(
        id = id,
        name = name,
        amount = amount,
        unit = unit,
        isChecked = isChecked,
        category = category,
        ingredientId = ingredient?.id,
    )
}