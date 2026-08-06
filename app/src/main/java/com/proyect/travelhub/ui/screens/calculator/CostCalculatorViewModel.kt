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
    }

    fun calculateCosts() {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val items = itineraryRepository.getTouristItinerary(uid)
            val sum = items.sumOf { it.cost }
            _totalCost.value = sum

            val grouped = items.groupBy { it.category }
            val breakdown = ServiceCategory.values().map { cat ->
                val catSum = grouped[cat]?.sumOf { it.cost } ?: 0.0
                val pct = if (sum > 0) (catSum / sum).toFloat() else 0f
                CategoryCostBreakdown(cat, catSum, pct)
            }
            _breakdownList.value = breakdown
            _isLoading.value = false
        }
    }
}