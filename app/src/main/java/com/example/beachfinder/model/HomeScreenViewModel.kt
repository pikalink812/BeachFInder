package com.example.beachfinder.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beachfinder.data.Beach
import com.example.beachfinder.data.Ocupation
import com.example.beachfinder.R // Asegúrate que R esté bien importado si usas drawables
import com.example.beachfinder.data.Facility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn

// Datos de ejemplo (reemplaza esto con tu fuente de datos real)
private val allBeachesSample = listOf(
    Beach("Playa Hermosa", "Costa del Sol, España", false, R.drawable.playacaballeros, Ocupation.BAJA, "Mediterráneo", 5.0, R.string.desc_caballeros, setOf(
        Facility.RESTAURANTES, Facility.PETFRIENDLY, Facility.DUCHAS, Facility.ALCOHOL), 120, 4, 36.5044, -4.8961),
    Beach("Arena Dorada", "Cancún, México", true, R.drawable.playacaballeros, Ocupation.ALTA, "Caribe", 10.2, R.string.desc_caballeros, setOf(), 350, 5, 21.1619, -86.8515),
    Beach("Mar Turquesa", "Maldivas", true, R.drawable.playacaballeros, Ocupation.MEDIA, "Índico", 2.5, R.string.desc_caballeros, setOf(), 210, 5, 3.2028, 73.2207),
    Beach("Cala Escondida", "Mallorca, España", false, R.drawable.playacaballeros, Ocupation.BAJA, "Mediterráneo", 3.1, R.string.desc_caballeros, setOf(), 90, 3, 39.5696, 2.6502),
    Beach("Playa Norte", "Isla Mujeres, México", true, R.drawable.playacaballeros, Ocupation.MEDIA, "Caribe", 8.0, R.string.desc_caballeros, setOf(), 180, 4, 21.2319, -86.7419),
    Beach("Los Roques", "Venezuela", true, R.drawable.playacaballeros, Ocupation.BAJA, "Caribe", 12.0, R.string.desc_caballeros, setOf(), 450, 5, 11.9554, -66.7536),
    Beach("Copacabana", "Río de Janeiro, Brasil", false, R.drawable.playacaballeros, Ocupation.ALTA, "Atlántico", 15.5, R.string.desc_caballeros, setOf(),1200, 4, -22.9719, -43.1823),
    Beach("Waikiki", "Oahu, Hawái", true, R.drawable.playacaballeros, Ocupation.MEDIA, "Pacífico", 20.0, R.string.desc_caballeros, setOf(),850, 4, 21.2793, -157.8292),
    Beach("Bondi Beach", "Sídney, Australia", false, R.drawable.playacaballeros, Ocupation.ALTA, "Pacífico", 18.2, R.string.desc_caballeros, setOf(), 700, 4, -33.8914, 151.2766),
    Beach("Anse Lazio", "Praslin, Seychelles", true, R.drawable.playacaballeros, Ocupation.BAJA, "Índico", 7.5, R.string.desc_caballeros, setOf(),320, 5, -4.2929, 55.7151)
)

@OptIn(FlowPreview::class)
// Enum para representar las vistas disponibles en la pantalla principal
enum class HomeScreenView {
    BEACHES_LIST,
    MAP
}

open class HomeScreenViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Estado para las facilities seleccionadas
    private val _selectedFacilities = MutableStateFlow<Set<Facility>>(emptySet())
    val selectedFacilities: StateFlow<Set<Facility>> = _selectedFacilities.asStateFlow()
    
    // Estado para la vista actual (lista o mapa)
    private val _currentView = MutableStateFlow(HomeScreenView.BEACHES_LIST)
    val currentView: StateFlow<HomeScreenView> = _currentView.asStateFlow()

    // Este sería el flujo original de todas las playas (podría venir de un repositorio)
    private val _allBeaches = MutableStateFlow(allBeachesSample)

    @OptIn(FlowPreview::class)
    val filteredBeaches: StateFlow<List<Beach>> =
        searchQuery
            .debounce(300) // Espera 300ms después de que el usuario deja de escribir
            .combine(_selectedFacilities) { query, facilities ->
                Pair(query, facilities)
            }
            .combine(_allBeaches) { (query, selectedFacilities), beaches ->
                beaches.filter { beach ->
                    // Filtrar por texto de búsqueda
                    val matchesQuery = query.isBlank() ||
                            beach.name.contains(query, ignoreCase = true) ||
                            beach.location.contains(query, ignoreCase = true)
                    
                    // Filtrar por facilities seleccionadas
                    val matchesFacilities = selectedFacilities.isEmpty() ||
                            selectedFacilities.any { facility -> beach.facilities.contains(facility) }
                    
                    // La playa debe cumplir ambos criterios
                    matchesQuery && matchesFacilities
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Mantiene el flujo activo por 5s
                initialValue = _allBeaches.value // Valor inicial antes de que el flujo combine emita
            )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Actualiza las instalaciones seleccionadas para filtrar las playas
     * @param facility La instalación a alternar (añadir o quitar)
     */
    fun toggleFacility(facility: Facility) {
        val currentFacilities = _selectedFacilities.value.toMutableSet()
        if (currentFacilities.contains(facility)) {
            currentFacilities.remove(facility)
        } else {
            currentFacilities.add(facility)
        }
        _selectedFacilities.value = currentFacilities
    }
    
    /**
     * Actualiza directamente el conjunto de instalaciones seleccionadas
     * @param facilities El nuevo conjunto de instalaciones seleccionadas
     */
    fun updateSelectedFacilities(facilities: Set<Facility>) {
        _selectedFacilities.value = facilities
    }
    
    /**
     * Actualiza la vista actual (lista o mapa)
     * @param view La nueva vista actual
     */
    fun updateCurrentView(view: HomeScreenView) {
        _currentView.value = view
    }

    fun getAllBeachesSample(): List<Beach> {
        return allBeachesSample
    }
}