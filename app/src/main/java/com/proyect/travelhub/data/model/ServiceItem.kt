package com.proyect.travelhub.data.model

enum class ServiceCategory {
    GUIA,
    HOSPEDAJE,
    ALIMENTACION,
    TRANSPORTE,
    TRADUCCION
}

data class ServiceItem(
    val id: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val title: String = "",
    val description: String = "",
    val category: ServiceCategory = ServiceCategory.HOSPEDAJE,
    val pricePerDayOrUnit: Double = 0.0,
    val rating: Double = 5.0,
    val reviewCount: Int = 0,
    val location: String = "Puno, Perú",
    val latitude: Double = -15.8402,
    val longitude: Double = -70.0219,
    val imageUrls: List<String> = emptyList(),
    val isAvailable: Boolean = true
)