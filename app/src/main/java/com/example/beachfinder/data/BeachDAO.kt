package com.example.beachfinder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BeachDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(beach: Beach)

    @Update
    suspend fun update(beach: Beach)

    @Query("SELECT * FROM beaches")
    fun getAllBeaches(): Flow<List<Beach>>
}