package com.izzatismail.mealmap.api

import com.izzatismail.mealmap.model.AddPlannedMealRequest
import com.izzatismail.mealmap.model.MealPlanDto
import com.izzatismail.mealmap.model.MealPlanRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MealPlanApi(
    private val httpClient: HttpClient,
) {
    suspend fun getMealPlans(): List<MealPlanDto> {
        return httpClient.get("${ApiConfig.baseUrl}/api/meal-plans").body()
    }

    suspend fun getMealPlanById(id: Long): MealPlanDto {
        return httpClient.get("${ApiConfig.baseUrl}/api/meal-plans/$id").body()
    }

    suspend fun createMealPlan(request: MealPlanRequest): MealPlanDto {
        return httpClient.post("${ApiConfig.baseUrl}/api/meal-plans") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun addMeal(planId: Long, request: AddPlannedMealRequest): MealPlanDto {
        return httpClient.post("${ApiConfig.baseUrl}/api/meal-plans/$planId/meals") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun removeMeal(planId: Long, mealId: Long): MealPlanDto {
        return httpClient.delete("${ApiConfig.baseUrl}/api/meal-plans/$planId/meals/$mealId").body()
    }

    suspend fun deleteMealPlan(id: Long) {
        httpClient.delete("${ApiConfig.baseUrl}/api/meal-plans/$id")
    }
}