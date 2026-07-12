package com.izzatismail.mealmap.config

import org.springframework.security.core.Authentication

object SecurityUtil {
    fun getCurrentUserId(authentication: Authentication): Long {
        val principal = authentication.principal
        if (principal is UserPrincipal) {
            return principal.userId
        }
        throw IllegalStateException("Unable to extract userId from authentication")
    }
}