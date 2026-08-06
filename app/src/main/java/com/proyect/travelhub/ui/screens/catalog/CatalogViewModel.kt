package com.proyect.travelhub.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyect.travelhub.data.model.Booking
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem
import com.proyect.travelhub.data.repository.BookingRepository
import com.proyect.travelhub.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val serviceRepository: ServiceRepository = ServiceRepository(),
    private val bookingRepository: BookingRepository = BookingRepository()
) : ViewModel() {

    private val _services = MutableStateFlow<List<ServiceItem>>(emptyList())
    val services: StateFlow<List<ServiceItem>> = _services

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
    val selectedCategory: StateFlow<ServiceCategory?> = _selectedCategory

    init {
        loadServices()
    }

    fun loadServices(category: ServiceCategory? = null) {
        _selectedCategory.value = category
        viewModelScope.launch {
            _isLoading.value = true
            var list = serviceRepository.getServices(category)
            if (list.isEmpty()) {
                seedDemoServices()
                list = serviceRepository.getServices(category)
            }
            _services.value = list
            _isLoading.value = false
        }
    }

    private suspend fun seedDemoServices() {
        val demo1 = ServiceItem(
            title = "Hotel Mirador del Titicaca",
            description = "Hospedaje confortable con vista panorámica al Lago Titicaca. Incluye desayuno buffet e internet alta velocidad.",
            category = ServiceCategory.HOSPEDAJE,
            pricePerDayOrUnit = 120.0,
            rating = 4.8,
            location = "Puno Centro",
            imageUrls = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800")
        )

        val demo2 = ServiceItem(
            title = "Restaurante Gourmet La Casona del Lago",
            description = "Platos típicos puneños, trucha fresca del Titicaca, caldo de carachi y cocina fusión andina.",
            category = ServiceCategory.ALIMENTACION,
            pricePerDayOrUnit = 45.0,
            rating = 4.9,
            location = "Plaza de Armas, Puno",
            imageUrls = listOf("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800")
        )

        val demo3 = ServiceItem(
            title = "Guía Bilingüe - Tour Uros y Taquile",
            description = "Tour guiado en español e inglés por las islas flotantes de los Uros y la milenaria isla de Taquile.",
            category = ServiceCategory.GUIA,
            pricePerDayOrUnit = 85.0,
            rating = 4.9,
            location = "Puerto de Puno",
            imageUrls = listOf("https://images.unsplash.com/photo-1526392060635-9d6019884377?w=800")
        )

        val demo4 = ServiceItem(
            title = "Transporte Turístico Privado Puno - Sillustani",
            description = "Servicio de movilidad privada ida y vuelta con chofer profesional a las Chullpas de Sillustani.",
            category = ServiceCategory.TRANSPORTE,
            pricePerDayOrUnit = 90.0,
            rating = 4.7,
            location = "Puno",
            imageUrls = listOf("https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800")
        )

        serviceRepository.addService(demo1)
        serviceRepository.addService(demo2)
        serviceRepository.addService(demo3)
        serviceRepository.addService(demo4)
    }
}