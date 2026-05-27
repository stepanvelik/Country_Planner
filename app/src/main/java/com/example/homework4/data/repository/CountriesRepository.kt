package com.example.homework4.data.repository

import com.example.homework4.data.model.Country
import com.example.homework4.data.model.CountryNote
import com.example.homework4.data.model.RecentCountry
import com.example.homework4.data.model.TravelStatus
import kotlinx.coroutines.flow.Flow

interface CountriesRepository {
    fun observeCountries(): Flow<List<Country>>
    suspend fun loadAll(): List<Country>
    suspend fun search(name: String): List<Country>
    suspend fun getCountryByCode(code: String): Country?
    suspend fun refreshCountries(): SyncResult
    suspend fun oldestCacheMillis(): Long?
    fun observeFavouriteCountries(): Flow<List<Country>>
    suspend fun getFavourites(): List<Country>
    suspend fun addFavourite(country: Country)
    suspend fun removeFavourite(country: Country)
    suspend fun isFavourite(code: String): Boolean
    fun observeRecentCountries(limit: Int = 20): Flow<List<RecentCountry>>
    suspend fun recordRecentCountry(country: Country)
    suspend fun clearRecentCountries()
    fun observeCountryNotes(): Flow<List<CountryNote>>
    fun observeCountryNote(code: String): Flow<CountryNote?>
    suspend fun saveCountryNote(country: Country, status: TravelStatus, note: String)
    suspend fun deleteCountryNote(code: String)
}
