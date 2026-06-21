package com.izzatismail.mealmap.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "pantry_items")
class PantryItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    val ingredient: Ingredient? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    var amount: Double,

    @Column(nullable = false)
    var unit: String,

    @Column(name = "expiration_date")
    val expirationDate: LocalDate? = null,

    @Column(name = "added_at", nullable = false)
    val addedAt: LocalDateTime = LocalDateTime.now(),
)