package com.example.homework4.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavouriteCountryEntity::class,
        CachedCountryEntity::class,
        RecentCountryEntity::class,
        CountryNoteEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favouritesDao(): FavouritesDao
    abstract fun cachedCountriesDao(): CachedCountriesDao
    abstract fun recentCountriesDao(): RecentCountriesDao
    abstract fun countryNotesDao(): CountryNotesDao
}
