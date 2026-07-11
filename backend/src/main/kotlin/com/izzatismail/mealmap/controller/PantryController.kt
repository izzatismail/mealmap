package com.izzatismail.mealmap.controller

import com.izzatismail.mealmap.config.SecurityUtil
import com.izzatismail.mealmap.dto.PantryItemDto
import com.izzatismail.mealmap.dto.PantryItemRequest
import com.izzatismail.mealmap.dto.UpdatePantryItemRequest
import com.izzatismail.mealmap.service.PantryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pantry")
class PantryController(
    private val pantryService: PantryService,
) {

    @GetMapping
    fun getPantryItems(authentication: Authentication): ResponseEntity<List<PantryItemDto>> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        return ResponseEntity.ok(pantryService.getPantryItems(userId))
    }

    @PostMapping
    fun addPantryItem(
        authentication: Authentication,
        @Valid @RequestBody request: PantryItemRequest,
    ): ResponseEntity<PantryItemDto> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        val item = pantryService.addPantryItem(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(item)
    }

    @PutMapping("/{id}")
    fun updatePantryItem(
        authentication: Authentication,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePantryItemRequest,
    ): ResponseEntity<PantryItemDto> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        val item = pantryService.updatePantryItem(userId, id, request)
        return ResponseEntity.ok(item)
    }

    @DeleteMapping("/{id}")
    fun deletePantryItem(
        authentication: Authentication,
        @PathVariable id: Long,
    ) {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        pantryService.deletePantryItem(userId, id)
    }
}