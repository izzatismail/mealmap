package com.izzatismail.mealmap.repository

import com.izzatismail.mealmap.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}