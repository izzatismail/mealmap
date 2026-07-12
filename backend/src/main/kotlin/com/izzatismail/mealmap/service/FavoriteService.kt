package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.dto.RecipeDto
import com.izzatismail.mealmap.entity.Favorite
import com.izzatismail.mealmap.exception.ResourceNotFoundException
import com.izzatismail.mealmap.repository.FavoriteRepository
import com.izzatismail.mealmap.repository.RecipeRepository
import com.izzatismail.mealmap.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FavoriteService(
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
) {

    fun getFavoriteRecipes(userId: Long): List<RecipeDto> {
        return favoriteRepository.findByUserIdOrderByFavoritedAtDesc(userId)
            .map { RecipeDto.fromEntity(it.recipe) }
    }

    fun getFavoriteRecipeIds(userId: Long): List<Long> {
        return favoriteRepository.findRecipeIdsByUserId(userId)
    }

    fun isFavorited(userId: Long, recipeId: Long): Boolean {
        return favoriteRepository.existsByUserIdAndRecipeId(userId, recipeId)
    }

    fun addFavorite(userId: Long, recipeId: Long): RecipeDto {
        if (favoriteRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            val recipe = recipeRepository.findById(recipeId)
                .orElseThrow { ResourceNotFoundException("Recipe not found with id: $recipeId") }
            return RecipeDto.fromEntity(recipe)
        }

        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }
        val recipe = recipeRepository.findById(recipeId)
            .orElseThrow { ResourceNotFoundException("Recipe not found with id: $recipeId") }

        favoriteRepository.save(Favorite(user = user, recipe = recipe))
        return RecipeDto.fromEntity(recipe)
    }

    fun removeFavorite(userId: Long, recipeId: Long) {
        val favorite = favoriteRepository.findByUserIdAndRecipeId(userId, recipeId)
            ?: throw ResourceNotFoundException("Favorite not found for recipe $recipeId")
        favoriteRepository.delete(favorite)
    }
}