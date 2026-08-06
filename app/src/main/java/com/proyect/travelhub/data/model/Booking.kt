package com.proyect.travelhub.data.model

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

data class Booking(
    val id: String = "",
    val touristId: String = "",
    val touristName: String = "",
    val providerId: String = "",
    val serviceId: String = "",
    val serviceTitle: String = "",
    val serviceCategory: ServiceCategory = ServiceCategory.HOSPEDAJE,
    val startDate: String = "",
    val endDate: String = "",
    val daysOrUnits: Int = 1,
    val totalPrice: Double = 0.0,
    val status: BookingStatus = BookingStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)