package com.izzatismail.mealmap.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "recipes")
class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val spoonacularId: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(length = 2048)
    val image: String = "",

    val imageType: String = "",

    val servings: Int = 0,

    val readyInMinutes: Int = 0,

    @Column(length = 2048)
    val sourceUrl: String = "",

    val sourceName: String = "",

    val creditsText: String = "",

    @Column(columnDefinition = "TEXT")
    val summary: String = "",

    @Column(columnDefinition = "TEXT")
    val instructions: String = "",

    val healthScore: Double = 0.0,

    val spoonacularScore: Double = 0.0,

    val pricePerServing: Double = 0.0,

    val cheap: Boolean = false,

    val dairyFree: Boolean = false,

    val glutenFree: Boolean = false,

    val vegan: Boolean = false,

    val vegetarian: Boolean = false,

    val veryHealthy: Boolean = false,

    val veryPopular: Boolean = false,

    val sustainable: Boolean = false,

    val whole30: Boolean = false,

    @Column(columnDefinition = "TEXT")
    val cuisines: String = "",

    @Column(columnDefinition = "TEXT")
    val dishTypes: String = "",

    @Column(columnDefinition = "TEXT")
    val diets: String = "",

    @Column(columnDefinition = "TEXT")
    val ingredients: String = "",

    @Column(name = "cached_at", nullable = false)
    val cachedAt: LocalDateTime = LocalDateTime.now(),
)