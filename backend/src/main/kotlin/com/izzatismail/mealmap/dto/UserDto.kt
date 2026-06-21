package com.izzatismail.mealmap.dto

import com.izzatismail.mealmap.entity.User
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val email: String,
    val name: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun fromEntity(user: User) = UserDto(
            id = user.id,
            email = user.email,
            name = user.name,
            createdAt = user.createdAt,
        )
    }
}