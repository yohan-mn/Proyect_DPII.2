package com.proyect.travelhub.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyect.travelhub.data.model.User
import com.proyect.travelhub.data.model.UserRole
import com.proyect.travelhub.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _resetPasswordState = MutableStateFlow<String?>(null)
    val resetPasswordState: StateFlow<String?> = _resetPasswordState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Por favor ingresa tu correo y contraseña")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginUser(email.trim(), pass)
            result.onSuccess { user ->
                _authState.value = AuthState.Success(user)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al iniciar sesión. Verifica tus datos.")
            }
        }
    }

    fun register(email: String, pass: String, name: String, role: UserRole, phone: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _authState.value = AuthState.Error("Por favor completa todos los campos requeridos")
            return
        }
        if (pass.length < 6) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.registerUser(email.trim(), pass, name.trim(), role, phone.trim())
            result.onSuccess { user ->
                _authState.value = AuthState.Success(user)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al crear la cuenta")
            }
        }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank()) {
            onResult(false, "Ingresa tu correo electrónico registrado")
            return
        }
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email.trim())
            result.onSuccess {
                onResult(true, "Te hemos enviado un enlace a $email para restablecer tu contraseña.")
            }.onFailure { error ->
                onResult(false, error.message ?: "No se pudo enviar el correo de recuperación")
            }
        }
    }

    fun loginWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithGoogleIdToken(idToken)
            result.onSuccess { user ->
                _authState.value = AuthState.Success(user)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al autenticar con Google")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}