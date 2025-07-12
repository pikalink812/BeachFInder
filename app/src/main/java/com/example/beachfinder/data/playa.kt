package com.example.beachfinder.data

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

data class Beach(
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
    val stars: Int
)