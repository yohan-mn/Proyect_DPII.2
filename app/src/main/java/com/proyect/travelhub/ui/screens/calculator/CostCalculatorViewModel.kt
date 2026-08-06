package com.proyect.travelhub.ui.screens.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyect.travelhub.data.model.ItineraryItem
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.repository.AuthRepository
import com.proyect.travelhub.data.repository.ItineraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.proyect.travelhub.data.repository.ReservationRepository

data class CategoryCostBreakdown(
    val category: ServiceCategory,
    val totalCost: Double,
    val percentage: Float
)

class CostCalculatorViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _totalCost = MutableStateFlow(0.0)
    val totalCost: StateFlow<Double> = _totalCost

    private val _breakdownList = MutableStateFlow<List<CategoryCostBreakdown>>(emptyList())
    val breakdownList: StateFlow<List<CategoryCostBreakdown>> = _breakdownList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {

        calculateCosts()

        viewModelScope.launch {

            ReservationRepository.reservations.collect {

                calculateCosts()

            }

        }

    }

    fun calculateCosts() {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val itineraryItems = itineraryRepository.getTouristItinerary(uid)

            val reservationItems =
                ReservationRepository.reservations.value

            val itineraryTotal =
                itineraryItems.sumOf { it.cost }

            val reservationTotal =
                reservationItems.sumOf { it.pricePerDayOrUnit }

            val sum =
                itineraryTotal + reservationTotal
            _totalCost.value = sum

            val itineraryGrouped =
                itineraryItems.groupBy { it.category }
            val breakdown = ServiceCategory.values().map { cat ->

                val itineraryCost =
                    itineraryGrouped[cat]?.sumOf { it.cost } ?: 0.0

                val reservationCost =
                    reservationItems
                        .filter { it.category == cat }
                        .sumOf { it.pricePerDayOrUnit }

                val categoryTotal =
                    itineraryCost + reservationCost

                val pct =
                    if (sum > 0)
                        (categoryTotal / sum).toFloat()
                    else
                        0f

                CategoryCostBreakdown(
                    cat,
                    categoryTotal,
                    pct
                )
            }
            _breakdownList.value = breakdown
            _isLoading.value = false
        }
    }
}