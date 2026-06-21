package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.Ingredient
import org.springframework.data.jpa.repository.JpaRepository

interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findByNameIgnoreCase(name: String): Ingredient?
}