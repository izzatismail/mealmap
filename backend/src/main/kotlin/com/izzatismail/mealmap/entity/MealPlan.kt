package com.izzatismail.mealmap.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "meal_plans")
class MealPlan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "week_start", nullable = false)
    val weekStart: LocalDate,

    @Column(name = "week_end", nullable = false)
    val weekEnd: LocalDate,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @OneToMany(mappedBy = "mealPlan", cascade = [CascadeType.ALL], orphanRemoval = true)
    val plannedMeals: MutableList<PlannedMeal> = mutableListOf()

    @OneToOne(mappedBy = "mealPlan", cascade = [CascadeType.ALL], orphanRemoval = true)
    var shoppingList: ShoppingList? = null
}