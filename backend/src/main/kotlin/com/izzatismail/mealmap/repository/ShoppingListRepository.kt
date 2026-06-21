package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.ShoppingList
import org.springframework.data.jpa.repository.JpaRepository

interface ShoppingListRepository : JpaRepository<ShoppingList, Long> {
    fun findByMealPlanId(mealPlanId: Long): ShoppingList?
    fun findByUserIdOrderByGeneratedAtDesc(userId: Long): List<ShoppingList>
}