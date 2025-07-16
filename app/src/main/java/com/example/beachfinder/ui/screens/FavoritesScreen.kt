package com.example.beachfinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.example.beachfinder.components.BeachCard
import com.example.beachfinder.model.BeachEntryViewModel
import com.example.beachfinder.model.HomeScreenView
import com.example.beachfinder.ui.components.AppScaffold

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: BeachEntryViewModel = viewModel()
) {
    // Obtener los datos de playas
    val beaches by viewModel.filteredBeaches.collectAsState()
    val favoriteBeaches = beaches.filter { beach -> beach.favourite }

    AppScaffold(
        navController = navController,
        title = "Favoritos"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Playas Favoritas",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (favoriteBeaches.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes playas favoritas aún.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteBeaches, key = { beach -> beach.name + beach.location }) { beach ->
                        BeachCard(beach = beach, onClick = { 
                            val encodedName = URLEncoder.encode(beach.name, StandardCharsets.UTF_8.toString())
                            navController.navigate("beachDetail/${encodedName}/FAVORITES")
                        })
                    }
                }
            }
        }
    }
}
