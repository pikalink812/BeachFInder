package com.example.beachfinder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.beachfinder.model.BeachEntryViewModel
import com.example.beachfinder.model.BeachViewModelFactory
import com.example.beachfinder.model.HomeScreenView
import com.example.beachfinder.ui.screens.AccountScreen
import com.example.beachfinder.ui.screens.BeachDetailScreen
import com.example.beachfinder.ui.screens.FavoritesScreen
import com.example.beachfinder.ui.screens.HomeScreen
import com.example.beachfinder.ui.screens.SettingsScreen
import com.example.beachfinder.ui.screens.TopBeachesScreen
import com.example.beachfinder.ui.theme.BeachFInderTheme
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object Destinations {
    const val HOME_ROUTE = "home"
    const val BEACH_DETAIL_ROUTE = "beachDetail/{beachName}/{fromView}"
    const val ACCOUNT_ROUTE = "account"
    const val FAVORITES_ROUTE = "favorites"
    const val TOP_BEACHES_ROUTE = "top_beaches"
    const val SETTINGS_ROUTE = "settings"

    // Helper function to create the detail route with an encoded beach name and fromView
    fun beachDetailRoute(beachName: String, fromView: String): String {
        return "beachDetail/$beachName/$fromView"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeachFInderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val beachViewModel: BeachEntryViewModel = viewModel(
                        factory = BeachViewModelFactory((context.applicationContext as BeachApplication).beachesRepository)
                    )

                    AppNavigation(navController = navController, beachViewModel = beachViewModel) // Pasar las variables necesarias
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Initialize BeachViewModel using the Factory for preview
    val beachViewModel: BeachEntryViewModel = viewModel(
        factory = BeachViewModelFactory((context.applicationContext as BeachApplication).beachesRepository)
    )
    
    BeachFInderTheme {
        AppNavigation(navController = navController, beachViewModel = beachViewModel)
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    beachViewModel: BeachEntryViewModel
) {
    // Ahora usamos los parámetros en lugar de crear nuevas instancias

    NavHost(navController = navController, startDestination = Destinations.HOME_ROUTE) {
        composable(Destinations.HOME_ROUTE) {
            // Pass the BeachViewModel to HomeScreen
            HomeScreen(
                viewModel = beachViewModel,
                onNavigateToDetail = { beach -> // Ahora recibe un objeto Beach completo
                    // Codificar el nombre de la playa para URL
                    val encodedBeachName = URLEncoder.encode(beach.name, StandardCharsets.UTF_8.toString())
                    // Obtener la vista actual para restaurarla al volver
                    val currentView = beachViewModel.currentView.value.toString()
                    navController.navigate(Destinations.beachDetailRoute(encodedBeachName, currentView))
                },
                navController = navController
            )
        }
        composable(Destinations.ACCOUNT_ROUTE) { AccountScreen(navController) }
        composable(Destinations.FAVORITES_ROUTE) { FavoritesScreen(navController) }
        composable(Destinations.TOP_BEACHES_ROUTE) { TopBeachesScreen(navController) }
        composable(Destinations.SETTINGS_ROUTE) { SettingsScreen(navController) }

        composable(
            route = Destinations.BEACH_DETAIL_ROUTE,
            arguments = listOf(
                navArgument("beachName") { type = NavType.StringType },
                navArgument("fromView") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedBeachName = backStackEntry.arguments?.getString("beachName")
            val fromView = backStackEntry.arguments?.getString("fromView")
            
            if (encodedBeachName != null) {
                // Decodificar el nombre de la playa
                val beachName = URLDecoder.decode(encodedBeachName, StandardCharsets.UTF_8.toString())
                Log.d("BeachDetailNav", "Navegando a detalle. Nombre de playa: $beachName, Vista origen: $fromView")

                // Recolecta todas las playas del ViewModel (asíncronamente)
                val beachListUiState by beachViewModel.beachListUiState.collectAsState()
                val allBeaches = beachListUiState.beachList

                // Buscar la playa por nombre (más confiable que por ID para recuperarla)
                val selectedBeach = allBeaches.find { it.name == beachName }

                if (selectedBeach != null) {
                    Log.d("BeachDetailNav", "Playa encontrada: ${selectedBeach.name}. Mostrando detalle.")

                    BeachDetailScreen(
                        beach = selectedBeach,
                        onBackClick = { 
                            // Restaurar la vista previa al volver
                            if (fromView != null) {
                                try {
                                    // Intentar convertir el string a HomeScreenView
                                    val previousView = HomeScreenView.valueOf(fromView)
                                    beachViewModel.updateCurrentView(previousView)
                                } catch (e: IllegalArgumentException) {
                                    // Por defecto a LIST si hay error
                                    beachViewModel.updateCurrentView(HomeScreenView.LIST)
                                }
                            }
                            navController.popBackStack() 
                        }
                    )
                } else {
                    Log.e("BeachDetailNav", "ERROR: Playa no encontrada con el nombre: $beachName. Navegando hacia atrás.")
                    navController.popBackStack()
                }
            } else {
                Log.e("BeachDetailNav", "ERROR: Argumento 'beachName' es nulo. Navegando hacia atrás.")
                navController.popBackStack()
            }
        }
    }
}