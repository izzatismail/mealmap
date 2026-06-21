package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.PlannedMeal
import org.springframework.data.jpa.repository.JpaRepository

interface PlannedMealRepository : JpaRepository<PlannedMeal, Long> {
    fun findByMealPlanId(mealPlanId: Long): List<PlannedMeal>
}