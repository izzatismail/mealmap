package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.PantryItem
import org.springframework.data.jpa.repository.JpaRepository

interface PantryItemRepository : JpaRepository<PantryItem, Long> {
    fun findByUserId(userId: Long): List<PantryItem>
    fun findByUserIdAndNameIgnoreCase(userId: Long, name: String): List<PantryItem>
}