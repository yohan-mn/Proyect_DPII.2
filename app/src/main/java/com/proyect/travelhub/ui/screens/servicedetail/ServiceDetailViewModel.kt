    package com.proyect.travelhub.ui.screens.servicedetail

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.proyect.travelhub.data.model.ServiceItem
    import com.proyect.travelhub.data.repository.ServiceRepository
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch

    class ServiceDetailViewModel(
        private val repository: ServiceRepository = ServiceRepository()
    ) : ViewModel() {

        private val _service = MutableStateFlow<ServiceItem?>(null)
        val service: StateFlow<ServiceItem?> = _service

        private val _loading = MutableStateFlow(false)
        val loading: StateFlow<Boolean> = _loading

        fun loadService(serviceId: String) {

            if (_service.value?.id == serviceId) return

            viewModelScope.launch {

                _loading.value = true

                _service.value = repository.getServiceById(serviceId)

                _loading.value = false

            }
        }
    }