package com.proyect.travelhub.data.repository

import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.model.ServiceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ReservationRepository {

    private val _reservations =
        MutableStateFlow<List<ServiceItem>>(emptyList())

    val reservations: StateFlow<List<ServiceItem>>
        get() = _reservations

    fun addReservation(service: ServiceItem) {

        if (_reservations.value.any { it.id == service.id })
            return

        _reservations.value =
            _reservations.value + service
    }

    fun removeReservation(serviceId: String) {

        _reservations.value =
            _reservations.value.filterNot {
                it.id == serviceId
            }

    }

    fun clearReservations() {

        _reservations.value = emptyList()

    }

    fun getTotalCost(): Double {

        return _reservations.value.sumOf {
            it.pricePerDayOrUnit
        }

    }

    fun getCategoryCost(
        category: ServiceCategory
    ): Double {

        return _reservations.value
            .filter {
                it.category == category
            }
            .sumOf {
                it.pricePerDayOrUnit
            }

    }

}