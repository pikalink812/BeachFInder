package com.example.beachfinder.data

import android.content.Context

interface AppContainer {
    val beachesRepository: BeachesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val beachesRepository: BeachesRepository by lazy {
        OfflineBeachesRepository(BeachDatabase.getDatabase(context).beachDao())
    }
}