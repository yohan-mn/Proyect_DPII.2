package com.proyect.travelhub.ui.screens.itinerary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.proyect.travelhub.data.model.ItineraryItem
import com.proyect.travelhub.data.model.ServiceCategory
import com.proyect.travelhub.data.repository.AuthRepository
import com.proyect.travelhub.data.repository.DirectionsRepository
import com.proyect.travelhub.data.repository.ItineraryRepository
import com.proyect.travelhub.data.repository.PlaceDetail
import com.proyect.travelhub.data.repository.PlaceSuggestion
import com.proyect.travelhub.data.repository.PlacesRepository
import com.proyect.travelhub.data.repository.RouteResult
import com.proyect.travelhub.data.repository.UserLocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItineraryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val itineraryRepository = ItineraryRepository()
    private val authRepository = AuthRepository()
    private val directionsRepository = DirectionsRepository()
    private val placesRepository = PlacesRepository(application.applicationContext)
    private val userLocationRepository = UserLocationRepository(application.applicationContext)

    private val _itineraryItems = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val itineraryItems: StateFlow<List<ItineraryItem>> = _itineraryItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _routeResult = MutableStateFlow(RouteResult())
    val routeResult: StateFlow<RouteResult> = _routeResult

    // Estado de la ubicación actual del turista
    private val _currentUserLocation = MutableStateFlow<LatLng?>(null)
    val currentUserLocation: StateFlow<LatLng?> = _currentUserLocation

    private val _useCurrentLocationAsStart = MutableStateFlow(true)
    val useCurrentLocationAsStart: StateFlow<Boolean> = _useCurrentLocationAsStart

    // Estados para la búsqueda de lugares
    private val _searchSuggestions = MutableStateFlow<List<PlaceSuggestion>>(emptyList())
    val searchSuggestions: StateFlow<List<PlaceSuggestion>> = _searchSuggestions

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _selectedPlace = MutableStateFlow<PlaceDetail?>(null)
    val selectedPlace: StateFlow<PlaceDetail?> = _selectedPlace

    private var searchJob: Job? = null

    init {
        fetchCurrentLocation()
        loadItinerary()
    }

    fun fetchCurrentLocation(onLocationFound: ((LatLng) -> Unit)? = null) {
        viewModelScope.launch {
            val loc = userLocationRepository.getCurrentLocation()
            _currentUserLocation.value = loc
            if (loc != null) {
                onLocationFound?.invoke(loc)
            }
            if (_itineraryItems.value.isNotEmpty()) {
                fetchRealRoute(_itineraryItems.value)
            }
        }
    }

    fun toggleUseCurrentLocation(enabled: Boolean) {
        _useCurrentLocationAsStart.value = enabled
        viewModelScope.launch {
            fetchRealRoute(_itineraryItems.value)
        }
    }

    fun loadItinerary() {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val list = itineraryRepository.getTouristItinerary(uid)
            _itineraryItems.value = list
            fetchRealRoute(list)
            _isLoading.value = false
        }
    }

    private suspend fun fetchRealRoute(items: List<ItineraryItem>) {
        if (items.isEmpty()) {
            _routeResult.value = RouteResult()
            return
        }

        val waypoints = mutableListOf<LatLng>()
        
        // Agregar ubicación actual del usuario como punto de partida si está activado
        val currentLoc = _currentUserLocation.value
        if (_useCurrentLocationAsStart.value && currentLoc != null) {
            waypoints.add(currentLoc)
        }

        items.forEach { waypoints.add(LatLng(it.latitude, it.longitude)) }

        if (waypoints.size < 2) {
            _routeResult.value = RouteResult()
            return
        }

        _routeResult.value = directionsRepository.getRoute(waypoints)
    }

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.length < 2) {
            _searchSuggestions.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _isSearching.value = true
            _searchSuggestions.value = placesRepository.searchPlaces(query)
            _isSearching.value = false
        }
    }

    fun onSuggestionSelected(suggestion: PlaceSuggestion) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchSuggestions.value = emptyList()
            val detail = placesRepository.getPlaceDetail(suggestion)
            _selectedPlace.value = detail
            _isSearching.value = false
        }
    }

    fun clearSelectedPlace() {
        _selectedPlace.value = null
        _searchSuggestions.value = emptyList()
    }

    fun addPlaceToItinerary(
        title: String,
        locationName: String,
        lat: Double,
        lng: Double,
        cost: Double,
        dayNumber: Int
    ) {
        val uid = authRepository.currentUserId ?: return
        val newItem = ItineraryItem(
            touristId = uid,
            dayNumber = dayNumber,
            title = title,
            description = "Visita a $locationName",
            locationName = locationName,
            latitude = lat,
            longitude = lng,
            cost = cost,
            category = ServiceCategory.GUIA
        )
        viewModelScope.launch {
            itineraryRepository.addItineraryItem(newItem)
            clearSelectedPlace()
            loadItinerary()
        }
    }

    fun removeItemFromItinerary(itemId: String) {
        viewModelScope.launch {
            itineraryRepository.deleteItineraryItem(itemId)
            loadItinerary()
        }
    }
}