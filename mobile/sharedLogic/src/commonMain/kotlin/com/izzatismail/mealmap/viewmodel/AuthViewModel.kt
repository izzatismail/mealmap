package com.izzatismail.mealmap.viewmodel

import com.izzatismail.mealmap.model.UserDto
import com.izzatismail.mealmap.repository.AuthRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: UserDto? = null,
    val isCheckingAuth: Boolean = true,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    constructor(authRepository: AuthRepository) : this(authRepository, CoroutineScope(SupervisorJob() + Dispatchers.Main))

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun checkAuth() {
        _state.value = AuthUiState(isCheckingAuth = true)
        scope.launch {
            val loggedIn = authRepository.isLoggedIn()
            _state.value = AuthUiState(isLoggedIn = loggedIn, isCheckingAuth = false)
        }
    }

    fun login(email: String, password: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val response = authRepository.login(email, password)
                _state.value = AuthUiState(
                    isLoggedIn = true,
                    isLoading = false,
                    user = response.user,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed",
                )
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val response = authRepository.register(email, password, name)
                _state.value = AuthUiState(
                    isLoggedIn = true,
                    isLoading = false,
                    user = response.user,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registration failed",
                )
            }
        }
    }

    fun logout() {
        scope.launch {
            authRepository.logout()
            _state.value = AuthUiState(isCheckingAuth = false)
        }
    }

    fun clear() {
        scope.cancel()
    }
}