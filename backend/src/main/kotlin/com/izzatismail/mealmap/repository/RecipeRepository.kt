package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.Recipe
import org.springframework.data.jpa.repository.JpaRepository

interface RecipeRepository : JpaRepository<Recipe, Long> {
    fun findBySpoonacularId(spoonacularId: Long): Recipe?
    fun findByTitleContainingIgnoreCase(title: String): List<Recipe>
}
