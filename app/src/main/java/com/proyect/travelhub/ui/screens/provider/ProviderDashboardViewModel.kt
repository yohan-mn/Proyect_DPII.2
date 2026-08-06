package com.proyect.travelhub.ui.screens.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem
import com.proyect.travelhub.data.repository.AuthRepository
import com.proyect.travelhub.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProviderDashboardViewModel(
    private val serviceRepository: ServiceRepository = ServiceRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _myServices = MutableStateFlow<List<ServiceItem>>(emptyList())
    val myServices: StateFlow<List<ServiceItem>> = _myServices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadMyServices()
    }

    fun loadMyServices() {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val list = serviceRepository.getServicesByProvider(uid)
            _myServices.value = list
            _isLoading.value = false
        }
    }

    fun publishService(
        title: String,
        desc: String,
        category: ServiceCategory,
        price: Double,
        location: String,
        imageUrl: String
    ) {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            val user = authRepository.getCurrentUserProfile()
            val images = if (imageUrl.isNotBlank()) listOf(imageUrl) else emptyList()
            val newService = ServiceItem(
                providerId = uid,
                providerName = user?.name ?: "Prestador Verificado",
                title = title,
                description = desc,
                category = category,
                pricePerDayOrUnit = price,
                location = location,
                imageUrls = images
            )
            serviceRepository.addService(newService)
            loadMyServices()
        }
    }
}