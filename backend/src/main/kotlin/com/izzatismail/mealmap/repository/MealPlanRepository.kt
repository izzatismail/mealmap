package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.MealPlan
import org.springframework.data.jpa.repository.JpaRepository

interface MealPlanRepository : JpaRepository<MealPlan, Long> {
    fun findByUserIdOrderByWeekStartDesc(userId: Long): List<MealPlan>
}