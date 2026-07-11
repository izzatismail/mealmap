package com.izzatismail.mealmap.controller

import com.izzatismail.mealmap.config.SecurityUtil
import com.izzatismail.mealmap.dto.RecipeDto
import com.izzatismail.mealmap.service.FavoriteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/favorites")
class FavoriteController(
    private val favoriteService: FavoriteService,
) {

    @GetMapping
    fun getFavorites(authentication: Authentication): ResponseEntity<List<RecipeDto>> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        return ResponseEntity.ok(favoriteService.getFavoriteRecipes(userId))
    }

    @GetMapping("/ids")
    fun getFavoriteIds(authentication: Authentication): ResponseEntity<List<Long>> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        return ResponseEntity.ok(favoriteService.getFavoriteRecipeIds(userId))
    }

    @GetMapping("/check/{recipeId}")
    fun isFavorited(
        authentication: Authentication,
        @PathVariable recipeId: Long,
    ): ResponseEntity<Map<String, Boolean>> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        val favorited = favoriteService.isFavorited(userId, recipeId)
        return ResponseEntity.ok(mapOf("favorited" to favorited))
    }

    @PostMapping("/{recipeId}")
    fun addFavorite(
        authentication: Authentication,
        @PathVariable recipeId: Long,
    ): ResponseEntity<RecipeDto> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        val recipe = favoriteService.addFavorite(userId, recipeId)
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe)
    }

    @DeleteMapping("/{recipeId}")
    fun removeFavorite(
        authentication: Authentication,
        @PathVariable recipeId: Long,
    ) {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        favoriteService.removeFavorite(userId, recipeId)
    }
}