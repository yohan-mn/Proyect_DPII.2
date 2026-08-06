package com.proyect.travelhub.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyect.travelhub.data.model.User
import com.proyect.travelhub.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = authRepository.getCurrentUserProfile()
            _userProfile.value = profile
            _isLoading.value = false
        }
    }

    fun updateProfile(name: String, phone: String, avatarUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = authRepository.updateUserProfile(name, phone, avatarUrl)
            res.onSuccess {
                _message.value = "Perfil actualizado correctamente"
                loadProfile()
            }.onFailure {
                _message.value = "Error al actualizar perfil"
            }
            _isLoading.value = false
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        authRepository.logout()
        onLogoutSuccess()
    }

    fun clearMessage() {
        _message.value = null
    }
}
