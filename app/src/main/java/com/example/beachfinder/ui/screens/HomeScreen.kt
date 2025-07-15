package com.example.beachfinder.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.beachfinder.components.BeachCard
import com.example.beachfinder.data.Beach
import com.example.beachfinder.data.Ocupation
import com.example.beachfinder.model.BeachEntryViewModel
import com.example.beachfinder.model.HomeScreenView
import com.example.beachfinder.ui.components.AppScaffold
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// Facility icon data class for the scrollable row
data class FacilityIconData(val label: String, val icon: ImageVector)

// Composable for the horizontally scrolling row of facility icons

@Composable
fun FacilityIconRow(
    selectedFacilities: Set<FacilityIconData>,
    onFacilityToggled: (FacilityIconData) -> Unit
) {
    val facilities = listOf(
        FacilityIconData("Pet-friendly", Icons.Default.Pets),
        FacilityIconData("Alcohol", Icons.Default.LocalBar),
        FacilityIconData("Fácil acceso", Icons.AutoMirrored.Filled.Accessible),
        FacilityIconData("Baño", Icons.Default.Wc),
        FacilityIconData("Restaurantes", Icons.Default.Restaurant),
        FacilityIconData("Duchas", Icons.Default.Shower),
        FacilityIconData("Parking", Icons.Default.LocalParking)
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(facilities) { facility ->
            FacilityIconItem(
                facility = facility,
                selected = selectedFacilities.contains(facility),
                onClick = { onFacilityToggled(facility) }
            )
        }
    }
}
@Composable
fun FacilityIconItem(
    facility: FacilityIconData,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (selected) Color(0xFFB3E5FC) else Color.White,
            border = if (selected) null else null,
            shadowElevation = if (selected) 4.dp else 1.dp,
            modifier = Modifier
                .size(48.dp)
                .clickable { onClick() }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = facility.icon,
                    contentDescription = facility.label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = facility.label,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BeachMapScreen(
    beaches: List<Beach>,
    onBeachSelected: (Beach) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFacilities: Set<FacilityIconData>,
    onFacilityToggled: (FacilityIconData) -> Unit,
    modifier: Modifier = Modifier
) {
    val mexicoCity = LatLng(19.4326, -99.1332) // Punto central inicial (CDMX)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mexicoCity, 5f)
    }
    
    // Banderas para controlar la visibilidad de filtros
    var showSearchBar by remember { mutableStateOf(false) }
    var showFacilityFilter by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Fila de botones para mostrar/ocultar filtros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón de búsqueda
            Button(
                onClick = { showSearchBar = !showSearchBar },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showSearchBar) MaterialTheme.colorScheme.primaryContainer 
                                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Búsqueda",
                    tint = if (showSearchBar) MaterialTheme.colorScheme.onPrimaryContainer 
                           else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Buscar", 
                    fontWeight = if (showSearchBar) FontWeight.Bold else FontWeight.Normal,
                    color = if (showSearchBar) MaterialTheme.colorScheme.onPrimaryContainer 
                           else MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Botón de filtros
            Button(
                onClick = { showFacilityFilter = !showFacilityFilter },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showFacilityFilter) MaterialTheme.colorScheme.primaryContainer 
                                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtros",
                    tint = if (showFacilityFilter) MaterialTheme.colorScheme.onPrimaryContainer 
                           else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Filtros", 
                    fontWeight = if (showFacilityFilter) FontWeight.Bold else FontWeight.Normal,
                    color = if (showFacilityFilter) MaterialTheme.colorScheme.onPrimaryContainer 
                           else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        // Barra de búsqueda condicional
        if (showSearchBar) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue -> onSearchQueryChange(newValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Buscar playa...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
        }
        
        // Fila de facilities condicional
        if (showFacilityFilter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                FacilityIconRow(
                    selectedFacilities = selectedFacilities,
                    onFacilityToggled = onFacilityToggled
                )
            }
        }
        
        // El mapa de Google
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = true,
                compassEnabled = true
            )
        ) {
            beaches.filter { it.latitude != 0.0 || it.longitude != 0.0 }.forEach { beach ->
                val position = LatLng(beach.latitude, beach.longitude)
                Marker(
                    state = MarkerState(position = position),
                    title = beach.name,
                    snippet = beach.location,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        when (beach.ocupation) {
                            Ocupation.BAJA -> BitmapDescriptorFactory.HUE_GREEN
                            Ocupation.MEDIA -> BitmapDescriptorFactory.HUE_ORANGE
                            Ocupation.ALTA -> BitmapDescriptorFactory.HUE_RED
                        }
                    ),
                    onClick = {
                        // Zoom in a la playa seleccionada
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 12f)
                        onBeachSelected(beach)
                        true
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: BeachEntryViewModel,
    onNavigateToDetail: (Beach) -> Unit,  // Cambiado para recibir un objeto Beach en lugar de solo ID
    navController: NavHostController
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredBeaches by viewModel.filteredBeaches.collectAsState()
    val currentView = viewModel.currentView.collectAsState().value
    val selectedFacilities by viewModel.selectedFacilityIcons.collectAsState() // Observa las facilidades seleccionadas

    AppScaffold(
        navController = navController,
        title = "BeachFinder",
        enableDrawerGestures = currentView != HomeScreenView.MAP, // Deshabilitar gestos cuando está en vista de mapa
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Lista") },
                    label = { Text("Lista") },
                    selected = currentView == HomeScreenView.LIST,
                    onClick = { viewModel.updateCurrentView(HomeScreenView.LIST) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                    label = { Text("Mapa") },
                    selected = currentView == HomeScreenView.MAP,
                    onClick = { viewModel.updateCurrentView(HomeScreenView.MAP) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentView) {
                HomeScreenView.LIST -> {
                    // Box para el buscador
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(color = Color(0xFFB3E5FC))
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { newValue -> viewModel.onSearchQueryChange(newValue) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(horizontal = 16.dp),
                            label = { Text("Buscar playa...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Box separado para FacilityIconRow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(color = Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        FacilityIconRow(
                            selectedFacilities = selectedFacilities,
                            onFacilityToggled = { facility ->
                                viewModel.onFacilityToggled(facility)
                            }
                        )
                    }

                    if (filteredBeaches.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay playas para mostrar.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredBeaches, key = {beach -> beach.name }) { beach ->
                                // Actualizado para pasar el objeto Beach completo
                                BeachCard(beach = beach, onClick = { onNavigateToDetail(beach) })
                            }
                        }
                    }
                }
                HomeScreenView.MAP -> {
                    // Implementación de la vista de mapa
                    BeachMapScreen(
                        beaches = filteredBeaches,
                        onBeachSelected = onNavigateToDetail,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { newValue -> viewModel.onSearchQueryChange(newValue) },
                        selectedFacilities = selectedFacilities,
                        onFacilityToggled = { facility -> viewModel.onFacilityToggled(facility) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


