package com.example.beachfinder

import android.app.Application
import com.example.beachfinder.data.BeachDatabase
import com.example.beachfinder.data.OfflineBeachesRepository

class BeachApplication : Application() {
    // Instancia perezosa de la base de datos
    val database: BeachDatabase by lazy { BeachDatabase.Companion.getDatabase(this) }
    // Instancia perezosa del repositorio
    val beachesRepository by lazy { OfflineBeachesRepository(database.beachDao()) }
}