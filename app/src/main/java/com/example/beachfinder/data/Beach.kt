package com.example.beachfinder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Ocupation{
    ALTA,
    MEDIA,
    BAJA
}

enum class Facility{
    PETFRIENDLY,
    ALCOHOL,
    FACILACCESO,
    BANIO,
    RESTAURANTES,
    DUCHAS,
    PARKING
}

@Entity(tableName = "beaches")
data class Beach(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val location: String,
    val favourite: Boolean,
    val image: Int,
    val ocupation: Ocupation,
    val sea: String,
    val windSpeed: Double,
    val description: Int,
    val facilities: Set<Facility>,
    val rating: Int,
    val stars: Int,
    val latitude: Double,
    val longitude: Double
)