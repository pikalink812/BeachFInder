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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.beachfinder.components.BeachCard
import com.example.beachfinder.data.Beach
import com.example.beachfinder.model.BeachViewModel
import com.example.beachfinder.ui.components.AppScaffold

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

sealed class Screen(val icon: @Composable () -> Unit, val label: String) {
    object Beaches : Screen({ Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }, "Playas")
    object Map : Screen({ Icon(Icons.Default.Map, contentDescription = null) }, "Mapa")
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: BeachViewModel,
    onNavigateToDetail: (Int) -> Unit,
    navController: NavHostController
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredBeaches by viewModel.filteredBeaches.collectAsState()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Beaches) }
    val selectedFacilities by viewModel.selectedFacilityIcons.collectAsState() // Observa las facilidades seleccionadas


    AppScaffold(
        navController = navController,
        title = "BeachFinder",
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = Screen.Beaches.icon,
                    label = { Text(Screen.Beaches.label) },
                    selected = currentScreen == Screen.Beaches,
                    onClick = { currentScreen = Screen.Beaches }
                )
                NavigationBarItem(
                    icon = Screen.Map.icon,
                    label = { Text(Screen.Map.label) },
                    selected = currentScreen == Screen.Map,
                    onClick = { currentScreen = Screen.Map }
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
                            items(filteredBeaches, key = {beach -> beach.id }) { beach ->
                                BeachCard(beach = beach, onClick = { onNavigateToDetail(beach.id) })
                            }
                        }
                    }
                }
                Screen.Map -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("El contenido del mapa aparecerá aquí", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}


