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
import com.example.beachfinder.model.BeachViewModel
import com.example.beachfinder.model.BeachViewModelFactory
import com.example.beachfinder.ui.screens.AccountScreen
import com.example.beachfinder.ui.screens.BeachDetailScreen
import com.example.beachfinder.ui.screens.FavoritesScreen
import com.example.beachfinder.ui.screens.HomeScreen
import com.example.beachfinder.ui.screens.SettingsScreen
import com.example.beachfinder.ui.screens.TopBeachesScreen
import com.example.beachfinder.ui.theme.BeachFInderTheme
import java.net.URLEncoder
import java.net.URLDecoder

object Destinations {
    const val HOME_ROUTE = "home"
    const val BEACH_DETAIL_ROUTE = "beachDetail/{beachId}"
    const val ACCOUNT_ROUTE = "account"
    const val FAVORITES_ROUTE = "favorites"
    const val TOP_BEACHES_ROUTE = "top_beaches"
    const val SETTINGS_ROUTE = "settings"

    // Helper function to create the detail route with an encoded beach name
    fun beachDetailRoute(beachId: Int): String {
        return "beachDetail/$beachId"
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
                    val beachViewModel: BeachViewModel = viewModel(
                        factory = BeachViewModelFactory((context.applicationContext as BeachApplication).beachesRepository)
                    )

                    AppNavigation() // Your navigation host
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize BeachViewModel using the Factory
    // This provides the repository from the Application class
    val beachViewModel: BeachViewModel = viewModel(
        factory = BeachViewModelFactory((context.applicationContext as BeachApplication).beachesRepository)
    )

    NavHost(navController = navController, startDestination = Destinations.HOME_ROUTE) {
        composable(Destinations.HOME_ROUTE) {
            // Pass the BeachViewModel to HomeScreen
            HomeScreen(
                viewModel = beachViewModel,
                onNavigateToDetail = { beachName -> // Pass only the beach name for navigation
                    navController.navigate(Destinations.beachDetailRoute(beachName))
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
            arguments = listOf(navArgument("beachId") { type = NavType.StringType })
        ) { backStackEntry ->
            val beachId = backStackEntry.arguments?.getInt("beachId")

            if (beachId != null) {
                Log.d("BeachDetailNav", "Navegando a detalle. ID de playa: $beachId")

                // Recolecta todas las playas del ViewModel (asíncronamente)
                val beachListUiState by beachViewModel.beachListUiState.collectAsState()
                val allBeaches = beachListUiState.beachList

                Log.d("BeachDetailNav", "Playas disponibles en el ViewModel para ID: ${allBeaches.map { it.id }}")

                // CAMBIO CLAVE: Busca la playa por ID
                val selectedBeach = allBeaches.find { it.id == beachId }


                if (selectedBeach != null) {
                    Log.d("BeachDetailNav", "Playa encontrada: ${selectedBeach.name}. Mostrando detalle.")

                    BeachDetailScreen(
                        beach = selectedBeach,
                        onBackClick = { navController.popBackStack() }
                    )
                } else {
                    Log.e("BeachDetailNav", "ERROR: Playa no encontrada con el nombre: $beachId. Navegando hacia atrás.")

                    // Handle case where beach is not found (e.g., show a Snackbar or navigate back)
                    navController.popBackStack()
                }
            } else {
                Log.e("BeachDetailNav", "ERROR: Argumento 'beachName' es nulo. Navegando hacia atrás.")

                navController.popBackStack()
            }
        }
    }
}