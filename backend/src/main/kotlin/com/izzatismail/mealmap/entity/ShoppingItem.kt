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

@Entity
@Table(name = "shopping_items")
class ShoppingItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    val shoppingList: ShoppingList,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    val ingredient: Ingredient? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val amount: Double,

    @Column(nullable = false)
    val unit: String,

    @Column(name = "is_checked", nullable = false)
    val isChecked: Boolean = false,

    val category: String = "",
)