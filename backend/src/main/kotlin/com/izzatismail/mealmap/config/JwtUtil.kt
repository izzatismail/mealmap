package com.izzatismail.mealmap.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(userId: Long, email: String): String {
        val now = Date()
        return Jwts.builder()
            .subject(email)
            .claim("userId", userId)
            .issuedAt(now)
            .expiration(Date(now.time + expiration))
            .signWith(key)
            .compact()
    }

    fun extractEmail(token: String): String? {
        return try {
            extractClaims(token).subject
        } catch (e: ExpiredJwtException) {
            log.debug("JWT token has expired")
            null
        } catch (e: JwtException) {
            log.debug("Failed to extract email from JWT: {}", e.message)
            null
        }
    }

    fun extractUserId(token: String): Long? {
        return try {
            extractClaims(token).get("userId", Long::class.java)
        } catch (e: ExpiredJwtException) {
            log.debug("JWT token has expired")
            null
        } catch (e: JwtException) {
            log.debug("Failed to extract userId from JWT: {}", e.message)
            null
        }
    }

    fun isValid(token: String, email: String): Boolean {
        return try {
            val claims = extractClaims(token)
            claims.subject == email && !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    private fun extractClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}