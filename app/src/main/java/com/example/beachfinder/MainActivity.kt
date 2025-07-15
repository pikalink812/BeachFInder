package com.example.beachfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.beachfinder.data.Beach
import com.example.beachfinder.model.HomeScreenView
import com.example.beachfinder.model.HomeScreenViewModel
import com.example.beachfinder.ui.screens.AccountScreen
import com.example.beachfinder.ui.screens.BeachDetailScreen
import com.example.beachfinder.ui.screens.FavoritesScreen
import com.example.beachfinder.ui.screens.HomeScreen
import com.example.beachfinder.ui.screens.SettingsScreen
import com.example.beachfinder.ui.screens.TopBeachesScreen
import com.example.beachfinder.ui.theme.BeachFInderTheme
import java.net.URLEncoder
import java.net.URLDecoder

// Define navigation routes
object Destinations {
    const val HOME_ROUTE = "home"
    const val BEACH_DETAIL_ROUTE = "beachDetail/{beachName}/{fromView}"
    const val ACCOUNT_ROUTE = "account"
    const val FAVORITES_ROUTE = "favorites"
    const val TOP_BEACHES_ROUTE = "top_beaches"
    const val SETTINGS_ROUTE = "settings"

    // Valores para el parámetro fromView
    const val VIEW_LIST = "list"
    const val VIEW_MAP = "map"

    fun beachDetailRoute(beachName: String, fromView: String) = "beachDetail/$beachName/$fromView"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeachFInderTheme {
                // Surface is needed here to apply the overall app background color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
    // Initialize the ViewModel here, it will be shared across Composables in this NavHost scope
    val homeScreenViewModel: HomeScreenViewModel = viewModel() // This will create or retrieve the ViewModel

    NavHost(navController = navController, startDestination = Destinations.HOME_ROUTE) {
        composable(Destinations.HOME_ROUTE) {
            HomeScreen(
                viewModel = homeScreenViewModel, // Pass the ViewModel
                onNavigateToDetail = { beach ->
                    // Encode the beach name to safely pass it as a URL argument
                    val encodedBeachName = URLEncoder.encode(beach.name, "UTF-8")
                    // Determinar la vista actual para saber a dónde volver
                    val currentView = homeScreenViewModel.currentView.value
                    val fromView = when (currentView) {
                        HomeScreenView.BEACHES_LIST -> Destinations.VIEW_LIST
                        HomeScreenView.MAP -> Destinations.VIEW_MAP
                    }
                    navController.navigate(Destinations.beachDetailRoute(encodedBeachName, fromView))
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
            val beachName = backStackEntry.arguments?.getString("beachName")
            val fromView = backStackEntry.arguments?.getString("fromView")
            if (beachName != null) {
                val decodedBeachName = URLDecoder.decode(beachName, "UTF-8")
                // Retrieve the beach object using the ViewModel's sample data
                val beach = homeScreenViewModel.getAllBeachesSample().find { it.name == decodedBeachName }
                if (beach != null && fromView != null) {
                    BeachDetailScreen(
                        beach = beach,
                        onBackClick = { 
                            // Actualizar la vista en el ViewModel según de dónde venimos
                            when (fromView) {
                                Destinations.VIEW_LIST -> homeScreenViewModel.updateCurrentView(HomeScreenView.BEACHES_LIST)
                                Destinations.VIEW_MAP -> homeScreenViewModel.updateCurrentView(HomeScreenView.MAP)
                            }
                            navController.popBackStack() 
                        }
                    )
                } else {
                    // Handle case where beach is not found (e.g., show error, navigate back)
                    // For simplicity, we'll navigate back if not found.
                    navController.popBackStack()
                }
            } else {
                // Handle case where argument is missing (shouldn't happen with correct navigation)
                navController.popBackStack()
            }
        }
    }
}