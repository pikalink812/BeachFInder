package com.example.beachfinder.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromOcupation(ocupation: Ocupation): String {
        return ocupation.name
    }

    @TypeConverter
    fun toOcupation(name: String): Ocupation {
        return Ocupation.valueOf(name)
    }

    @TypeConverter
    fun fromFacilitySet(facilities: Set<Facility>): String {
        return Gson().toJson(facilities.map { it.name })
    }

    @TypeConverter
    fun toFacilitySet(facilitiesString: String): Set<Facility> {
        val type = object : TypeToken<List<String>>() {}.type
        val list: List<String> = Gson().fromJson(facilitiesString, type)
        return list.map { Facility.valueOf(it) }.toSet()
    }
}