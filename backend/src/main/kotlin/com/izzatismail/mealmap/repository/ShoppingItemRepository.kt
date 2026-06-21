package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.ShoppingItem
import org.springframework.data.jpa.repository.JpaRepository

interface ShoppingItemRepository : JpaRepository<ShoppingItem, Long> {
    fun findByShoppingListId(shoppingListId: Long): List<ShoppingItem>
}