package com.izzatismail.mealmap.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header = request.getHeader(AUTHORIZATION_HEADER)

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.removePrefix(BEARER_PREFIX)
        val email = jwtUtil.extractEmail(token)

        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            if (jwtUtil.isValid(token, email)) {
                val userDetails = userDetailsService.loadUserByUsername(email)
                val userId = jwtUtil.extractUserId(token) ?: 0L
                val principal = UserPrincipal(
                    username = userDetails.username,
                    password = userDetails.password,
                    userId = userId,
                    authorities = userDetails.authorities,
                )
                val authToken = UsernamePasswordAuthenticationToken(
                    principal, null, principal.authorities
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }
}