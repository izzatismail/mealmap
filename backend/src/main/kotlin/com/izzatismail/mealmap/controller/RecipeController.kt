package com.izzatismail.mealmap.controller

import com.izzatismail.mealmap.dto.RecipeDto
import com.izzatismail.mealmap.service.SpoonacularService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/recipes")
class RecipeController(
    private val spoonacularService: SpoonacularService,
) {

    @GetMapping("/search")
    fun searchRecipes(
        @RequestParam query: String,
        @RequestParam(defaultValue = "10") limit: Int,
    ): ResponseEntity<List<RecipeDto>> {
        val results = spoonacularService.searchRecipes(query, limit)
        return ResponseEntity.ok(results)
    }

    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Long): ResponseEntity<RecipeDto> {
        val result = spoonacularService.getRecipeById(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/by-ingredients")
    fun findByIngredients(
        @RequestParam ingredients: List<String>,
    ): ResponseEntity<List<RecipeDto>> {
        val results = spoonacularService.findByIngredients(ingredients)
        return ResponseEntity.ok(results)
    }

    @GetMapping("/cached")
    fun getCachedRecipes(): ResponseEntity<List<RecipeDto>> {
        val results = spoonacularService.getCachedRecipes()
        return ResponseEntity.ok(results)
    }
}