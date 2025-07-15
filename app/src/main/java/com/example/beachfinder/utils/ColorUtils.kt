package com.example.beachfinder.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.beachfinder.data.Ocupation

/**
 * Devuelve un color basado en el nivel de ocupación de una playa
 * - BAJA: Verde (baja ocupación)
 * - MEDIA: Naranja (ocupación media)
 * - ALTA: Rojo (alta ocupación)
 */
@Composable
fun getOccupationColor(ocupation: Ocupation): Color {
    return when (ocupation) {
        Ocupation.BAJA -> Color(0xFF4CAF50) // Verde
        Ocupation.MEDIA -> Color(0xFFFFC107) // Naranja
        Ocupation.ALTA -> Color(0xFFF44336) // Rojo
    }
}


fun getOccupationColorValue(ocupation: Ocupation): Int {
    return when (ocupation) {
        Ocupation.BAJA -> 0xFF4CAF50.toInt() // Verde
        Ocupation.MEDIA -> 0xFFFFC107.toInt() // Naranja
        Ocupation.ALTA -> 0xFFF44336.toInt() // Rojo
    }
}
