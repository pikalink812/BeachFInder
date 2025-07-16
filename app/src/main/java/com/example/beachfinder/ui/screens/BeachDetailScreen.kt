package com.example.beachfinder.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.beachfinder.R
import com.example.beachfinder.data.Beach
import com.example.beachfinder.data.Facility
import com.example.beachfinder.data.Ocupation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeachDetailScreen(
    beach: Beach?, 
    onBackClick: () -> Unit, 
    modifier: Modifier = Modifier,
    onFavoriteToggle: ((Beach) -> Unit)? = null 
) {
    if (beach == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Error: No se pudo cargar la información de la playa.")
            // SideEffect para volver atrás automáticamente si no se carga.
            LaunchedEffect(Unit) {
                onBackClick()
            }
        }
        return // Importante para no intentar acceder a `beach` si es null
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Mantener el estado local para una respuesta inmediata en la UI
                    var isFavorite by remember { mutableStateOf(beach.favourite) }
                    
                    IconButton(onClick = { 
                        // Cambiar el estado local para actualización inmediata
                        isFavorite = !isFavorite
                        
                        // Crear una copia de la playa con el estado de favorito actualizado
                        val updatedBeach = beach.copy(favourite = isFavorite)
                        
                        // Llamar al callback para actualizar la base de datos
                        onFavoriteToggle?.invoke(updatedBeach)
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (isFavorite) Color(0xFFE91E63) else LocalContentColor.current
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f), // Hacer un poco transparente
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Permite el scroll del contenido
                .background(MaterialTheme.colorScheme.background) // Fondo general de la pantalla
        ) {
            // Imagen de la playa
            Image(
                painter = painterResource(id = beach.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            // Contenido principal de la información
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp) 
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(MaterialTheme.colorScheme.surface) 
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // Nombre y Ubicación 
                Text(
                    text = beach.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Ubicación",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = beach.location,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(16.dp))

                // Current Status (Ocupación)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Estado actual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Círculo de color según la ocupación
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(getOccupationColor(beach.ocupation)) 
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${beach.ocupation.name.lowercase().replaceFirstChar { it.uppercase() }} ocupación", 
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Mar y Viento (Weather Conditions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Card Mar
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.wave_image), 
                                    contentDescription = "Mar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Mar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = beach.sea,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Card Viento
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.wind_image), 
                                    contentDescription = "Viento",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Viento",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${beach.windSpeed} km/h", 
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Description
                Card() {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = stringResource(beach.description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Facilities
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Facilidades",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column {
                            beach.facilities.forEach { facility ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Check, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = facility.toString(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Botón para abrir Google Maps
                val context = LocalContext.current
                Button(
                    onClick = {
                        // Crear una URI para Google Maps con la ubicación de la playa
                        val gmmIntentUri = Uri.parse("geo:${beach.latitude},${beach.longitude}?q=${beach.latitude},${beach.longitude}(${Uri.encode(beach.name)})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        
                        // Verificar si Google Maps está instalado
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            // Si Google Maps no está instalado, abrir en el navegador
                            val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${beach.latitude},${beach.longitude}")
                            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.Filled.Map,
                        contentDescription = "Ver en Google Maps",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ver en Google Maps")
                }
            }
        }
    }
}

// Función auxiliar para obtener el color de ocupación
@Composable
fun getOccupationColor(ocupation: Ocupation): Color {
    return when (ocupation) {
        Ocupation.BAJA -> Color(0xFF4CAF50) // Verde
        Ocupation.MEDIA -> Color(0xFFFFC107) // Naranja
        Ocupation.ALTA -> Color(0xFFF44336) // Rojo
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun BeachDetailScreenPreview() {
    val sampleBeach = Beach(
        name = "Playa Caballeros",
        location = "Punta Hermosa, Lima",
        favourite = true,
        image = R.drawable.playacaballeros, // URL de ejemplo para Coil
        ocupation = Ocupation.MEDIA,
        sea = "Olas grandes",
        windSpeed = 18.0,
        description = R.string.desc_playa_1,
        facilities = setOf(Facility.RESTAURANTES, Facility.ALCOHOL), // No relevante para este preview específico
        rating = 156,
        stars = 5,
        latitude = 0.0,
        longitude = 0.0
    )
    MaterialTheme {
        BeachDetailScreen(beach = sampleBeach, onBackClick = {})
    }
}