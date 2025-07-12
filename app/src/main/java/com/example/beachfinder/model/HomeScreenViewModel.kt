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
        Facility.RESTAURANTES, Facility.PETFRIENDLY, Facility.DUCHAS, Facility.ALCOHOL), 120, 4),
    Beach("Arena Dorada", "Cancún, México", true, R.drawable.playacaballeros, Ocupation.ALTA, "Caribe", 10.2, R.string.desc_caballeros, setOf(), 350, 5),
    Beach("Mar Turquesa", "Maldivas", true, R.drawable.playacaballeros, Ocupation.MEDIA, "Índico", 2.5, R.string.desc_caballeros, setOf(), 210, 5),
    Beach("Cala Escondida", "Mallorca, España", false, R.drawable.playacaballeros, Ocupation.BAJA, "Mediterráneo", 3.1, R.string.desc_caballeros, setOf(), 90, 3),
    Beach("Playa Norte", "Isla Mujeres, México", true, R.drawable.playacaballeros, Ocupation.MEDIA, "Caribe", 8.0, R.string.desc_caballeros, setOf(), 180, 4),
    Beach("Los Roques", "Venezuela", true, R.drawable.playacaballeros, Ocupation.BAJA, "Caribe", 12.0, R.string.desc_caballeros, setOf(), 450, 5),
    Beach("Copacabana", "Río de Janeiro, Brasil", false, R.drawable.playacaballeros, Ocupation.ALTA, "Atlántico", 15.5, R.string.desc_caballeros, setOf(),1200, 4),
    Beach("Waikiki", "Oahu, Hawái", true, R.drawable.playacaballeros, Ocupation.MEDIA, "Pacífico", 20.0, R.string.desc_caballeros, setOf(),850, 4),
    Beach("Bondi Beach", "Sídney, Australia", false, R.drawable.playacaballeros, Ocupation.ALTA, "Pacífico", 18.2, R.string.desc_caballeros, setOf(), 700, 4),
    Beach("Anse Lazio", "Praslin, Seychelles", true, R.drawable.playacaballeros, Ocupation.BAJA, "Índico", 7.5, R.string.desc_caballeros, setOf(),320, 5)
)

@OptIn(FlowPreview::class)
open class HomeScreenViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Este sería el flujo original de todas las playas (podría venir de un repositorio)
    private val _allBeaches = MutableStateFlow(allBeachesSample)

    val filteredBeaches: StateFlow<List<Beach>> =
        searchQuery
            .debounce(300) // Espera 300ms después de que el usuario deja de escribir
            .combine(_allBeaches) { query, beaches ->
                if (query.isBlank()) {
                    beaches
                } else {
                    beaches.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.location.contains(query, ignoreCase = true)
                    }
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

    fun getAllBeachesSample(): List<Beach> {
        return allBeachesSample
    }
}