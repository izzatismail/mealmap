package com.izzatismail.mealmap.dto.auth

import com.izzatismail.mealmap.dto.UserDto

data class AuthResponse(
    val token: String,
    val user: UserDto,
)