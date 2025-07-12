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
import com.example.beachfinder.data.Ocupation // Asume que tienes este enum

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
                painterResource(R.drawable.playacaballeros),
                contentDescription = "Image of ${beach.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f), // Ajusta según la proporción deseada de tus imágenes
                contentScale = ContentScale.Crop // Crop para llenar el espacio y cortar si es necesario
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
                    horizontalArrangement = Arrangement.SpaceBetween // Para separar nombre y ocupación
                ) {
                    Text(
                        text = beach.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f) // Permite que el nombre ocupe el espacio disponible
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Espacio entre nombre y punto
                    OcupationIndicator(ocupation = beach.ocupation)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        // Mantenemos el texto "Ocupación:" aquí para claridad, aunque el indicador visual ya está arriba
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
                        tint = MaterialTheme.colorScheme.primary, // Puedes ajustar el color
                        modifier = Modifier.size(18.dp) // Ajusta el tamaño del icono
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = beach.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fila para Rating y Estrellas (como estaba antes)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {


                    // Asumiendo que tienes un campo 'rating' y 'numberOfStars' en tu clase Beach
                    // Si no los tienes, puedes adaptarlo o quitarlo.
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
            .size(12.dp) // Tamaño del círculo
            .clip(CircleShape)
            .background(color)
    )
}

// Datos de ejemplo para el Preview
// Asegúrate de tener tu clase Beach y Ocupation definidas como en el contexto previo.
@Preview(showBackground = true)
@Composable
fun BeachCardPreview() {
    val sampleBeach = Beach(
        name = "Playa Caballeros",
        location = "Punta Hermosa, Lima",
        favourite = true,
        image = R.drawable.playacaballeros, // URL de ejemplo para Coil
        ocupation = Ocupation.MEDIA,
        sea = "Olas grandes",
        windSpeed = 18.0,
        description = R.string.desc_caballeros,
        facilities = setOf(), // No relevante para este preview específico
        rating = 156,
        stars = 5
    )
    MaterialTheme { // Necesitas un tema para los previews de Material3
        BeachCard(beach = sampleBeach, onClick = {})
    }
}
