package com.izzatismail.mealmap.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank @field:Email
    val email: String,

    @field:NotBlank @field:Size(min = 6, message = "Password must be at least 6 characters")
    val password: String,

    @field:NotBlank
    val name: String,
)