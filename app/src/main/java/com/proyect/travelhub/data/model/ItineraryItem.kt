package com.proyect.travelhub.data.model

data class ItineraryItem(
    val id: String = "",
    val touristId: String = "",
    val dayNumber: Int = 1,
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val cost: Double = 0.0,
    val category: ServiceCategory = ServiceCategory.GUIA,
    val bookingId: String? = null
)