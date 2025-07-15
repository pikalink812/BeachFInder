package com.example.beachfinder.data

import kotlinx.coroutines.flow.Flow

class OfflineBeachesRepository(private val beachDao: BeachDAO) : BeachesRepository {

    override fun getAllBeachesStream(): Flow<List<Beach>> = beachDao.getAllBeaches()

    override suspend fun updateBeach(beach: Beach) = beachDao.update(beach)


}