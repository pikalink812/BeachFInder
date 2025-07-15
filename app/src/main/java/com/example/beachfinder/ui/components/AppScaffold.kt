package com.example.beachfinder.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.beachfinder.Destinations
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavController,
    title: String,
    bottomBar: @Composable () -> Unit = {},
    enableDrawerGestures: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        "Inicio" to Destinations.HOME_ROUTE,
        "Mi Cuenta" to Destinations.ACCOUNT_ROUTE,
        "Favoritos" to Destinations.FAVORITES_ROUTE,
        "Mejores Playas" to Destinations.TOP_BEACHES_ROUTE,
        "Configuración" to Destinations.SETTINGS_ROUTE
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = enableDrawerGestures,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                drawerItems.forEach { (itemTitle, itemRoute) ->
                    NavigationDrawerItem(
                        label = { Text(itemTitle) },
                        selected = currentRoute == itemRoute,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != itemRoute) {
                                navController.navigate(itemRoute)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            },
            bottomBar = bottomBar,
            content = content
        )
    }
}
