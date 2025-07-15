package com.example.beachfinder.data

import android.content.ClipData
import kotlinx.coroutines.flow.Flow

interface BeachesRepository {
    fun getAllBeachesStream(): Flow<List<Beach>>

    suspend fun updateBeach(beach: Beach)
}