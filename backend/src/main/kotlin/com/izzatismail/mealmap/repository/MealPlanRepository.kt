package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.MealPlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MealPlanRepository : JpaRepository<MealPlan, Long> {
    fun findByUserIdOrderByWeekStartDesc(userId: Long): List<MealPlan>

    @Query("SELECT DISTINCT mp FROM MealPlan mp LEFT JOIN FETCH mp.plannedMeals pm LEFT JOIN FETCH pm.recipe WHERE mp.id = :id")
    fun findByIdWithPlannedMeals(@Param("id") id: Long): MealPlan?
}