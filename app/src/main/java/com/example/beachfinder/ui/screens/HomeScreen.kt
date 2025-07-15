package com.example.beachfinder.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.beachfinder.components.BeachCard
import com.example.beachfinder.data.Beach
import com.example.beachfinder.data.Facility
import com.example.beachfinder.data.Ocupation
import com.example.beachfinder.model.HomeScreenView
import com.example.beachfinder.model.HomeScreenViewModel
import com.example.beachfinder.ui.components.AppScaffold
import com.example.beachfinder.utils.getOccupationColor

// Facility icon data class for the scrollable row
data class FacilityIconData(val label: String, val icon: ImageVector, val facility: Facility)

// Composable for the horizontally scrolling row of facility icons

@Composable
fun FacilityIconRow(
    selectedFacilities: Set<FacilityIconData>,
    onFacilityToggled: (FacilityIconData) -> Unit
) {
    val facilities = listOf(
        FacilityIconData("Pet-friendly", Icons.Default.Pets, Facility.PETFRIENDLY),
        FacilityIconData("Alcohol", Icons.Default.LocalBar, Facility.ALCOHOL),
        FacilityIconData("Fácil acceso", Icons.AutoMirrored.Filled.Accessible, Facility.FACILACCESO),
        FacilityIconData("Baño", Icons.Default.Wc, Facility.BANIO),
        FacilityIconData("Restaurantes", Icons.Default.Restaurant, Facility.RESTAURANTES),
        FacilityIconData("Duchas", Icons.Default.Shower, Facility.DUCHAS),
        FacilityIconData("Parking", Icons.Default.LocalParking, Facility.PARKING)
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

sealed class Screen(val icon: @Composable () -> Unit, val label: String) {
    object Beaches : Screen({ Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }, "Playas")
    object Map : Screen({ Icon(Icons.Default.Map, contentDescription = null) }, "Mapa")
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(),
    onNavigateToDetail: (Beach) -> Unit,
    navController: NavController
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredBeaches by viewModel.filteredBeaches.collectAsState()
    val selectedFacilitiesFromVM by viewModel.selectedFacilities.collectAsState()
    val currentViewFromVM by viewModel.currentView.collectAsState()
    
    // Mapeo entre HomeScreenView y Screen
    val currentScreen by remember(currentViewFromVM) { 
        mutableStateOf<Screen>(
            when (currentViewFromVM) {
                HomeScreenView.BEACHES_LIST -> Screen.Beaches
                HomeScreenView.MAP -> Screen.Map
            }
        )
    }
    
    // Convertir las Facility seleccionadas en el ViewModel a FacilityIconData para la UI
    val facilityMapping = remember {
        mapOf<Facility, FacilityIconData>(
            Facility.PETFRIENDLY to FacilityIconData("Pet-friendly", Icons.Default.Pets, Facility.PETFRIENDLY),
            Facility.ALCOHOL to FacilityIconData("Alcohol", Icons.Default.LocalBar, Facility.ALCOHOL),
            Facility.FACILACCESO to FacilityIconData("Fácil acceso", Icons.AutoMirrored.Filled.Accessible, Facility.FACILACCESO),
            Facility.BANIO to FacilityIconData("Baño", Icons.Default.Wc, Facility.BANIO),
            Facility.RESTAURANTES to FacilityIconData("Restaurantes", Icons.Default.Restaurant, Facility.RESTAURANTES),
            Facility.DUCHAS to FacilityIconData("Duchas", Icons.Default.Shower, Facility.DUCHAS),
            Facility.PARKING to FacilityIconData("Parking", Icons.Default.LocalParking, Facility.PARKING)
        )
    }
    
    val selectedFacilities = selectedFacilitiesFromVM.mapNotNull { facilityMapping[it] }.toSet()

    AppScaffold(
        navController = navController,
        title = "BeachFinder",
        disableDrawerGestures = currentScreen == Screen.Map,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = Screen.Beaches.icon,
                    label = { Text(Screen.Beaches.label) },
                    selected = currentScreen == Screen.Beaches,
                    onClick = { 
                        viewModel.updateCurrentView(HomeScreenView.BEACHES_LIST)
                    }
                )
                NavigationBarItem(
                    icon = Screen.Map.icon,
                    label = { Text(Screen.Map.label) },
                    selected = currentScreen == Screen.Map,
                    onClick = { 
                        viewModel.updateCurrentView(HomeScreenView.MAP)
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentScreen) {
                Screen.Beaches -> {
                    // Box para el buscador
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(color = Color(0xFFB3E5FC))
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
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
                            onFacilityToggled = { facilityIconData ->
                                viewModel.toggleFacility(facilityIconData.facility)
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
                            items(filteredBeaches, key = { beach -> beach.name + beach.location }) { beach ->
                                BeachCard(beach = beach, onClick = onNavigateToDetail)
                            }
                        }
                    }
                }
                Screen.Map -> {
                    // Implementación del mapa de Google
                    // Coordenadas iniciales (centro de México)
                    val initialPosition = LatLng(19.4326, -99.1332)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(initialPosition, 5f)
                    }
                    
                    // Usar la lista filtrada de playas del ViewModel
                    // (ya filtrada por búsqueda y facilities seleccionadas)
                    
                    // Filtrar solo las playas que tienen coordenadas válidas
                    val beachesWithCoordinates = filteredBeaches.filter { beach ->
                        beach.latitude != 0.0 || beach.longitude != 0.0
                    }
                    
                    Box(modifier = Modifier.fillMaxSize()) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(
                                isMyLocationEnabled = false,
                                mapType = com.google.maps.android.compose.MapType.NORMAL,
                                isTrafficEnabled = false
                            ),
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = true,
                                myLocationButtonEnabled = false,
                                compassEnabled = true,
                                mapToolbarEnabled = true,
                                zoomGesturesEnabled = true,
                                scrollGesturesEnabled = true,
                                rotationGesturesEnabled = true,
                                tiltGesturesEnabled = true
                            )
                        ) {
                            // Añadir marcadores para cada playa
                            beachesWithCoordinates.forEach { beach ->
                                val position = LatLng(beach.latitude, beach.longitude)
                                
                                // Crear un marcador personalizado con el color correspondiente a la ocupación
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
                                        // Navegar a los detalles de la playa
                                        cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 12f)
                                        // Navegar a la pantalla de detalles
                                        onNavigateToDetail(beach)
                                        true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        HomeScreen(
            viewModel = HomeScreenViewModel(),
            onNavigateToDetail = {},
            navController = navController
        )
    }
}
