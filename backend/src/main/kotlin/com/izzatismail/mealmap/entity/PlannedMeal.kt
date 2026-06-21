package com.izzatismail.mealmap.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "planned_meals")
class PlannedMeal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    val mealPlan: MealPlan,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    val recipe: Recipe,

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    val mealType: MealType,

    @Column(name = "day_of_week", nullable = false)
    val dayOfWeek: Int,

    @Column(nullable = false)
    val servings: Int = 1,
)