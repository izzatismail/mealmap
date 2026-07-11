package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.Favorite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun findByUserIdOrderByFavoritedAtDesc(userId: Long): List<Favorite>

    fun findByUserIdAndRecipeId(userId: Long, recipeId: Long): Favorite?

    fun existsByUserIdAndRecipeId(userId: Long, recipeId: Long): Boolean

    @Query("SELECT f.recipe.id FROM Favorite f WHERE f.user.id = :userId")
    fun findRecipeIdsByUserId(@Param("userId") userId: Long): List<Long>
}