package com.example.beachfinder.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.beachfinder.R // Asume que tienes un placeholder en drawable
import com.example.beachfinder.data.Beach
import com.example.beachfinder.data.Ocupation 

@Composable
fun BeachCard(
    beach: Beach,
    onClick: (Beach) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable{onClick(beach)}
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column {
            // Imagen: Ocupa todo el ancho y 2/3 del alto disponible para la imagen
            // Para controlar la altura de la imagen en proporción al Card,
            // normalmente necesitarías que el Card tenga una altura definida o usar BoxWithConstraints.
            // Aquí, para simplificar, le daremos una altura fija al Card o a la imagen.
            // Una forma más robusta sería pasar la altura deseada o calcularla dinámicamente.
            // Por ahora, usaremos aspectRatio para la imagen para que mantenga proporciones.
            Image(
                painter = painterResource(beach.image),
                contentDescription = "Image of ${beach.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f), 
                contentScale = ContentScale.Crop 
            )

            // Contenido debajo de la imagen
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) {
                    Text(
                        text = beach.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f) 
                    )
                    Spacer(modifier = Modifier.width(8.dp)) 
                    OcupationIndicator(ocupation = beach.ocupation)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${beach.ocupation.name.lowercase().replaceFirstChar { it.uppercase() }} ocupación",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Fila para el Icono de Ubicación y el Texto de Ubicación
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location icon",
                        tint = MaterialTheme.colorScheme.primary, 
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = beach.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fila para Rating y Estrellas 
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {



                    Text(
                        text = "${"★".repeat(beach.stars)}${"☆".repeat(5 - beach.stars)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF5CA5D)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${beach.rating} reviews)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun OcupationIndicator(ocupation: Ocupation, modifier: Modifier = Modifier) {
    val pastelGreen = Color(0xFFC8E6C9) // Un verde pastel
    val pastelOrange = Color(0xFFFFE0B2) // Un naranja/ámbar pastel
    val pastelRed = Color(0xFFFFCDD2)    // Un rojo/rosa pastel

    val color = when (ocupation) {
        Ocupation.BAJA -> pastelGreen
        Ocupation.MEDIA -> pastelOrange
        Ocupation.ALTA -> pastelRed
    }
    Box(
        modifier = modifier
            .size(12.dp) 
            .clip(CircleShape)
            .background(color)
    )
}

// Datos de ejemplo para el Preview

@Composable
fun BeachCardPreview() {
    val sampleBeach = Beach(
        name = "Playa Caballeros",
        location = "Punta Hermosa, Lima",
        favourite = true,
        image = R.drawable.playacaballeros, 
        ocupation = Ocupation.MEDIA,
        sea = "Olas grandes",
        windSpeed = 18.0,
        description = R.string.desc_playa_1,
        facilities = setOf(), 
        rating = 156,
        stars = 5,
        latitude = 0.0,
        longitude = 0.0
    )
    MaterialTheme { 
        BeachCard(beach = sampleBeach, onClick = {})
    }
}
