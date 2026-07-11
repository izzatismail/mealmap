package com.izzatismail.mealmap.config

import com.izzatismail.mealmap.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val appUser = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")

        return User.withUsername(appUser.email)
            .password(appUser.password)
            .build()
    }
}