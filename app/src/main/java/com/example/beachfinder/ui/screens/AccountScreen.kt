package com.example.beachfinder.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.beachfinder.ui.components.AppScaffold

@Composable
fun AccountScreen(navController: NavController) {
    AppScaffold(
        navController = navController,
        title = "Mi Cuenta"
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Contenido de Mi Cuenta")
        }
    }
}
