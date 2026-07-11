package com.izzatismail.mealmap.controller

import com.izzatismail.mealmap.config.SecurityUtil
import com.izzatismail.mealmap.dto.CreateMealPlanRequest
import com.izzatismail.mealmap.dto.MealPlanDto
import com.izzatismail.mealmap.service.MealPlanService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/meal-plans")
class MealPlanController(
    private val mealPlanService: MealPlanService,
) {

    @GetMapping
    fun getMealPlans(authentication: Authentication): ResponseEntity<List<MealPlanDto>> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        return ResponseEntity.ok(mealPlanService.getMealPlans(userId))
    }

    @GetMapping("/{id}")
    fun getMealPlanById(@PathVariable id: Long): ResponseEntity<MealPlanDto> {
        return ResponseEntity.ok(mealPlanService.getMealPlanById(id))
    }

    @PostMapping
    fun createMealPlan(
        authentication: Authentication,
        @Valid @RequestBody request: CreateMealPlanRequest,
    ): ResponseEntity<MealPlanDto> {
        val userId = SecurityUtil.getCurrentUserId(authentication)
        val mealPlan = mealPlanService.createMealPlan(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(mealPlan)
    }

    @DeleteMapping("/{id}")
    fun deleteMealPlan(@PathVariable id: Long) {
        mealPlanService.deleteMealPlan(id)
    }
}