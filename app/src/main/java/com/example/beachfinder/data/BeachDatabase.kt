package com.example.beachfinder.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.beachfinder.R

@Database(entities = [Beach::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BeachDatabase : RoomDatabase() {
    abstract fun beachDao(): BeachDAO

    companion object {
        @Volatile
        private var Instance: BeachDatabase? = null

        fun getDatabase(context: Context): BeachDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, BeachDatabase::class.java, "beach_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(BeachDatabaseCallback(context))
                    .build()
                    .also { Instance = it }
            }
        }
    }

    private class BeachDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("BeachDatabase", "onCreate de la base de datos llamado. Intentando pre-poblar.") // ¡Añade esta línea!
            CoroutineScope(Dispatchers.IO).launch {
                val dao = Instance?.beachDao() ?: return@launch
                populateDatabase(dao)
                Log.d("BeachDatabase", "Pre-población de la base de datos finalizada.")
            }
        }

        private suspend fun populateDatabase(beachDao: BeachDAO) {
            val sampleBeaches = listOf(
                Beach(
                    name = "Playa Waikiki",
                    location = "Miraflores, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros, // Placeholder
                    ocupation = Ocupation.ALTA,
                    sea = "Pacífico",
                    windSpeed = 8.0,
                    description = R.string.desc_playa_1, // Placeholder
                    facilities = setOf(Facility.FACILACCESO, Facility.RESTAURANTES),
                    rating = 4,
                    stars = 4,
                    latitude = -12.1264,
                    longitude = -77.0379
                ),
                Beach(
                    name = "Playa Punta Hermosa",
                    location = "Punta Hermosa, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.MEDIA,
                    sea = "Pacífico",
                    windSpeed = 12.5,
                    description = R.string.desc_2,
                    facilities = setOf(Facility.RESTAURANTES, Facility.PARKING, Facility.DUCHAS),
                    rating = 4,
                    stars = 4,
                    latitude = -12.3391,
                    longitude = -76.8125
                ),
                Beach(
                    name = "Playa El Silencio",
                    location = "Punta Hermosa, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.ALTA,
                    sea = "Pacífico",
                    windSpeed = 10.0,
                    description = R.string.desc_3,
                    facilities = setOf(Facility.RESTAURANTES, Facility.PARKING),
                    rating = 3,
                    stars = 3,
                    latitude = -12.3490,
                    longitude = -76.7970
                ),
                Beach(
                    name = "Playa Señoritas",
                    location = "Punta Hermosa, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.MEDIA,
                    sea = "Pacífico",
                    windSpeed = 11.0,
                    description = R.string.desc_4,
                    facilities = setOf(Facility.RESTAURANTES),
                    rating = 4,
                    stars = 4,
                    latitude = -12.3551,
                    longitude = -76.7909
                ),
                Beach(
                    name = "Playa Caballeros",
                    location = "Punta Hermosa, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.MEDIA,
                    sea = "Pacífico",
                    windSpeed = 11.0,
                    description = R.string.desc_5,
                    facilities = setOf(Facility.RESTAURANTES),
                    rating = 4,
                    stars = 4,
                    latitude = -12.3562,
                    longitude = -76.7898
                ),
                Beach(
                    name = "Playa San Bartolo",
                    location = "San Bartolo, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.MEDIA,
                    sea = "Pacífico",
                    windSpeed = 9.5,
                    description = R.string.desc_6,
                    facilities = setOf(Facility.RESTAURANTES, Facility.PARKING, Facility.BANIO),
                    rating = 4,
                    stars = 4,
                    latitude = -12.3810,
                    longitude = -76.7620
                ),
                Beach(
                    name = "Playa Santa María del Mar",
                    location = "Santa María del Mar, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.BAJA,
                    sea = "Pacífico",
                    windSpeed = 7.0,
                    description = R.string.desc_7,
                    facilities = setOf(Facility.PARKING, Facility.BANIO),
                    rating = 5,
                    stars = 5,
                    latitude = -12.4410,
                    longitude = -76.7580
                ),
                Beach(
                    name = "Playa Naplo",
                    location = "Punta Hermosa, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.BAJA,
                    sea = "Pacífico",
                    windSpeed = 6.0,
                    description = R.string.desc_8,
                    facilities = setOf(Facility.RESTAURANTES),
                    rating = 4,
                    stars = 4,
                    latitude = -12.3660,
                    longitude = -76.7800
                ),
                Beach(
                    name = "Playa Pescadores",
                    location = "Chorrillos, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.ALTA,
                    sea = "Pacífico",
                    windSpeed = 9.0,
                    description = R.string.desc_9,
                    facilities = setOf(Facility.FACILACCESO, Facility.RESTAURANTES),
                    rating = 3,
                    stars = 3,
                    latitude = -12.1620,
                    longitude = -77.0270
                ),
                Beach(
                    name = "Playa La Pampilla",
                    location = "Miraflores, Lima",
                    favourite = false,
                    image = R.drawable.playacaballeros,
                    ocupation = Ocupation.ALTA,
                    sea = "Pacífico",
                    windSpeed = 10.0,
                    description = R.string.desc_10,
                    facilities = setOf(Facility.FACILACCESO, Facility.RESTAURANTES),
                    rating = 3,
                    stars = 3,
                    latitude = -12.1150,
                    longitude = -77.0420
                )
            )

            sampleBeaches.forEach { beach ->
                beachDao.insert(beach)
            }
        }
    }
}