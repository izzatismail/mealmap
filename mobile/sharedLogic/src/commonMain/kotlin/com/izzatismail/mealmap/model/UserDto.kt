package com.izzatismail.mealmap.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val email: String,
    val name: String,
    val createdAt: String = "",
)