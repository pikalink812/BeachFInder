package com.example.beachfinder.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.beachfinder.data.Beach
import com.example.beachfinder.data.BeachesRepository
import com.example.beachfinder.data.Facility
import com.example.beachfinder.ui.screens.FacilityIconData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Enum para la vista actual de la HomeScreen
enum class HomeScreenView {
    LIST, MAP
}

fun mapFacilityIconDataToFacility(iconData: FacilityIconData): Facility {
    return when (iconData.label) {
        "Pet-friendly" -> Facility.PETFRIENDLY
        "Alcohol" -> Facility.ALCOHOL
        "Fácil acceso" -> Facility.FACILACCESO
        "Baño" -> Facility.BANIO
        "Restaurantes" -> Facility.RESTAURANTES
        "Duchas" -> Facility.DUCHAS
        "Parking" -> Facility.PARKING
        else -> throw IllegalArgumentException("Unknown facility label: ${iconData.label}")
    }
}

class BeachEntryViewModel(private val beachesRepository: BeachesRepository) : ViewModel() {

    // Representa el estado de la UI para la lista de playas (incluye la lista real de la BD)
    // Se inicializa con un estado de carga
    private val _beachListUiState = MutableStateFlow(BeachListUiState(isLoading = true))
    val beachListUiState: StateFlow<BeachListUiState> = _beachListUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFacilityIcons = MutableStateFlow<Set<FacilityIconData>>(emptySet())
    val selectedFacilityIcons: StateFlow<Set<FacilityIconData>> = _selectedFacilityIcons.asStateFlow()
    
    // Estado para mantener la vista actual (lista o mapa)
    private val _currentView = MutableStateFlow(HomeScreenView.LIST)
    val currentView: StateFlow<HomeScreenView> = _currentView.asStateFlow()

    init {
        // Inicia la carga de playas desde el repositorio (BD) cuando el ViewModel se crea
        Log.d("BeachViewModel", "ViewModel inicializado. Iniciando carga de playas desde DB...")
        viewModelScope.launch {
            beachesRepository.getAllBeachesStream().collect { beaches ->
                Log.d("BeachViewModel", "Playas cargadas desde DB: ${beaches.size} playas.")
                _beachListUiState.value = BeachListUiState(beachList = beaches, isLoading = false)
            }
        }
    }

    fun updateBeach(beach: Beach) {
        viewModelScope.launch {
            beachesRepository.updateBeach(beach)
        }
    }

    fun onFacilityToggled(facilityIconData: FacilityIconData) {
        _selectedFacilityIcons.update { currentSet ->
            if (currentSet.contains(facilityIconData)) {
                currentSet - facilityIconData
            } else {
                currentSet + facilityIconData
            }
        }
    }

    // El filteredBeaches se combina con _beachListUiState para obtener los datos de la BD
    val filteredBeaches: StateFlow<List<Beach>> =
        combine(
            _beachListUiState.map { it.beachList }, // Obtiene solo la lista de playas del UI State
            searchQuery, // Estado de la búsqueda
            _selectedFacilityIcons // Estado de las facilidades seleccionadas
        ) { beaches, query, selectedIcons ->
            val selectedFacilitiesEnums = selectedIcons.map { mapFacilityIconDataToFacility(it) }.toSet()

            beaches.filter { beach ->
                val matchesSearchQuery = beach.name.contains(query, ignoreCase = true) ||
                        beach.location.contains(query, ignoreCase = true)
                val matchesFacilities = if (selectedFacilitiesEnums.isEmpty()) {
                    true
                } else {
                    beach.facilities.containsAll(selectedFacilitiesEnums)
                }
                matchesSearchQuery && matchesFacilities
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    // Método para actualizar la vista actual (lista o mapa)
    fun updateCurrentView(view: HomeScreenView) {
        _currentView.value = view
    }

    // ¡Eliminar esta función! Ya no se necesitan datos de muestra hardcodeados.
    // fun getAllBeachesSample(): List<Beach> {
    //     return allBeachesSample
    // }
}

// Clase para representar el estado de la UI de la lista de playas
data class BeachListUiState(
    val beachList: List<Beach> = listOf(),
    val isLoading: Boolean = false, // Añadido para indicar si los datos están cargando
    val error: String? = null // Añadido para manejar errores
)

// Factory para instanciar el ViewModel con el repositorio
class BeachViewModelFactory(private val beachesRepository: BeachesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeachEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeachEntryViewModel(beachesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}