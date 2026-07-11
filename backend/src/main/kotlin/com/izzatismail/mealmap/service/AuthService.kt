package com.izzatismail.mealmap.service

import com.izzatismail.mealmap.config.JwtUtil
import com.izzatismail.mealmap.dto.UserDto
import com.izzatismail.mealmap.dto.auth.AuthResponse
import com.izzatismail.mealmap.dto.auth.LoginRequest
import com.izzatismail.mealmap.dto.auth.RegisterRequest
import com.izzatismail.mealmap.entity.User
import com.izzatismail.mealmap.exception.ResourceNotFoundException
import com.izzatismail.mealmap.repository.UserRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
) {

    fun register(request: RegisterRequest): AuthResponse {
        val existing = userRepository.findByEmail(request.email)
        if (existing != null) {
            throw IllegalArgumentException("Email is already registered")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
        )
        val saved = userRepository.save(user)
        val token = jwtUtil.generateToken(saved.id, saved.email)
        return AuthResponse(token = token, user = saved.toDto())
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw BadCredentialsException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BadCredentialsException("Invalid email or password")
        }

        val token = jwtUtil.generateToken(user.id, user.email)
        return AuthResponse(token = token, user = user.toDto())
    }

    private fun User.toDto() = UserDto(
        id = id,
        email = email,
        name = name,
        createdAt = createdAt,
    )
}