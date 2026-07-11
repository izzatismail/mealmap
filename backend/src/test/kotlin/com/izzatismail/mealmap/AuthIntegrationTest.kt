package com.izzatismail.mealmap

import com.fasterxml.jackson.databind.ObjectMapper
import com.izzatismail.mealmap.dto.auth.AuthResponse
import com.izzatismail.mealmap.dto.auth.LoginRequest
import com.izzatismail.mealmap.dto.auth.RegisterRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private fun uniqueEmail(): String = "test-${UUID.randomUUID()}@example.com"

    @Test
    fun `register creates user and returns token`() {
        val email = uniqueEmail()
        val request = RegisterRequest(email = email, password = "password123", name = "Test User")

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.token").isNotEmpty)
            .andExpect(jsonPath("$.user.email").value(email))
            .andExpect(jsonPath("$.user.name").value("Test User"))
    }

    @Test
    fun `register with duplicate email returns 400`() {
        val email = uniqueEmail()
        val request = RegisterRequest(email = email, password = "password123", name = "Dup")

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `login with valid credentials returns token`() {
        val email = uniqueEmail()
        val password = "password123"

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RegisterRequest(email = email, password = password, name = "Login Test")))
        )
            .andExpect(status().isCreated)

        val loginRequest = LoginRequest(email = email, password = password)
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isNotEmpty)
            .andExpect(jsonPath("$.user.email").value(email))
    }

    @Test
    fun `login with wrong password returns 401`() {
        val email = uniqueEmail()

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RegisterRequest(email = email, password = "password123", name = "Wrong PW")))
        )
            .andExpect(status().isCreated)

        val loginRequest = LoginRequest(email = email, password = "wrongpassword")
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `protected endpoint returns 401 without token`() {
        mockMvc.perform(get("/api/favorites"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `protected endpoint returns 200 with valid token`() {
        val email = uniqueEmail()
        val registerResult = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RegisterRequest(email = email, password = "password123", name = "Auth Test")))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val responseBody = registerResult.response.contentAsString
        val authResponse = objectMapper.readValue(responseBody, AuthResponse::class.java)

        mockMvc.perform(
            get("/api/favorites")
                .header("Authorization", "Bearer ${authResponse.token}")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `register with invalid email returns 400`() {
        val request = RegisterRequest(email = "not-an-email", password = "password123", name = "Bad Email")

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `register with short password returns 400`() {
        val request = RegisterRequest(email = uniqueEmail(), password = "123", name = "Short PW")

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }
}