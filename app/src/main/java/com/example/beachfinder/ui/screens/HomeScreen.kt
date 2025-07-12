package com.example.beachfinder.ui.screens // O la ruta de tu paquete de UI

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.beachfinder.components.BeachCard // Asegúrate que la ruta sea correcta
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.setValue
import com.example.beachfinder.model.HomeScreenViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beachfinder.data.Beach

// Define a sealed class to represent the different screens
sealed class Screen(val route: String, val icon: @Composable () -> Unit, val label: String) {
    object Beaches : Screen("beaches", { Icon(Icons.Filled.List, contentDescription = null) }, "Playas")
    object Map : Screen("map", { Icon(Icons.Filled.Map, contentDescription = null) }, "Mapa")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(), // Inyecta el ViewModel
    onNavigateToDetail: (Beach) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredBeaches by viewModel.filteredBeaches.collectAsState()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Beaches) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BeachFinder") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                // Beaches button
                NavigationBarItem(
                    icon = { Screen.Beaches.icon() },
                    label = { Text(Screen.Beaches.label) },
                    selected = currentScreen == Screen.Beaches,
                    onClick = { currentScreen = Screen.Beaches }
                )
                // Map button
                NavigationBarItem(
                    icon = { Screen.Map.icon() },
                    label = { Text(Screen.Map.label) },
                    selected = currentScreen == Screen.Map,
                    onClick = { currentScreen = Screen.Map }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding) // Aplica el padding del Scaffold
                .fillMaxSize()
        ) {
            when (currentScreen) {
                Screen.Beaches -> {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Buscar playas...") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )

                    // List of Beaches
                    if (filteredBeaches.isEmpty()) {
                        if (searchQuery.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No se encontraron playas para \"$searchQuery\".",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
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
                    // Placeholder for the Map Screen content
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Contenido del Mapa aquí",
                            style = MaterialTheme.typography.headlineMedium
                        )
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
    // Usamos el tema de la app para el preview
    // Si tienes un archivo Theme.kt, envuélvelo así:
    // com.example.beachfinder.ui.theme.BeachFInderTheme { // Ajusta el nombre de tu tema
    MaterialTheme { // O usa MaterialTheme directamente si no tienes un tema customizado complejo
        HomeScreen(viewModel = PreviewHomeScreenViewModel(),
            onNavigateToDetail = {}
        )
    }
    // }
}

// Preview for the BottomAppBar
@Preview(showBackground = true)
@Composable
fun BottomAppBarPreview() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Beaches) } // Simulate selected screen
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Screen.Beaches.icon() },
                        label = { Text(Screen.Beaches.label) },
                        selected = currentScreen == Screen.Beaches,
                        onClick = { currentScreen = Screen.Beaches }
                    )
                    NavigationBarItem(
                        icon = { Screen.Map.icon() },
                        label = { Text(Screen.Map.label) },
                        selected = currentScreen == Screen.Map,
                        onClick = { currentScreen = Screen.Map }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Content behind BottomAppBar for preview")
            }
        }
    }
}

// Un ViewModel de mentira para el Preview, para no depender de la lógica real de Koin/Hilt o datos de red
class PreviewHomeScreenViewModel : HomeScreenViewModel() {
    // Puedes sobreescribir los StateFlows si quieres un estado específico en el preview
    // Por ejemplo, para mostrar una lista no vacía inmediatamente:
    init {
        // Esto es un poco un hack para previews, en ViewModel real los datos vendrían de otra forma.
        // La lógica de `filteredBeaches` en el ViewModel base ya usa `allBeachesSample`
        // por lo que el preview debería mostrar la lista por defecto.
    }
}
