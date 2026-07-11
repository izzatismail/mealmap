package com.izzatismail.mealmap.controller

import com.izzatismail.mealmap.config.SecurityUtil
import com.izzatismail.mealmap.dto.ShoppingItemDto
import com.izzatismail.mealmap.dto.ShoppingListDto
import com.izzatismail.mealmap.service.ShoppingListGeneratorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ShoppingListController(
    private val shoppingListGeneratorService: ShoppingListGeneratorService,
) {

    @PostMapping("/meal-plans/{mealPlanId}/shopping-list")
    fun generateShoppingList(
        @PathVariable mealPlanId: Long,
        @RequestParam(defaultValue = "false") regenerate: Boolean,
    ): ResponseEntity<ShoppingListDto> {
        val shoppingList = shoppingListGeneratorService.generateShoppingList(mealPlanId, regenerate)
        return ResponseEntity.status(HttpStatus.CREATED).body(shoppingList)
    }

    @GetMapping("/shopping-lists/{id}")
    fun getShoppingList(@PathVariable id: Long): ResponseEntity<ShoppingListDto> {
        return ResponseEntity.ok(shoppingListGeneratorService.getShoppingList(id))
    }

    @GetMapping("/shopping-lists/current")
    fun getCurrentShoppingList(authentication: Authentication): ResponseEntity<ShoppingListDto?> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        return ResponseEntity.ok(shoppingListGeneratorService.getLatestShoppingList(userId))
    }

    @PatchMapping("/shopping-items/{itemId}/toggle")
    fun toggleShoppingItem(@PathVariable itemId: Long): ResponseEntity<ShoppingItemDto> {
        val item = shoppingListGeneratorService.toggleItem(itemId)
        return ResponseEntity.ok(item)
    }
}