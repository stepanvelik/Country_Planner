package com.example.homework4.fakes

import com.example.homework4.data.model.Country
import com.example.homework4.data.model.CountryNote
import com.example.homework4.data.model.RecentCountry
import com.example.homework4.data.model.TravelStatus
import com.example.homework4.data.repository.CountriesRepository
import com.example.homework4.data.repository.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCountriesRepository : CountriesRepository {

    var loadAllData: List<Country> = emptyList()
        set(value) {
            field = value
            countriesFlow.value = value
            lastCacheMillis = if (value.isEmpty()) null else currentTimeMillis
        }

    var loadAllError: Throwable? = null
    val searchCalls = mutableListOf<String>()
    var currentTimeMillis: Long = 1_000L
    var lastCacheMillis: Long? = null
    var refreshCalls = 0

    private val countriesFlow = MutableStateFlow<List<Country>>(emptyList())
    private val favourites = mutableListOf<Country>()
    private val favouritesFlow = MutableStateFlow<List<Country>>(emptyList())
    private val recentFlow = MutableStateFlow<List<RecentCountry>>(emptyList())
    private val notesFlow = MutableStateFlow<List<CountryNote>>(emptyList())

    var getCountryByCodeHandler: (String) -> Country? = { code ->
        loadAllData.find { it.code == code }
    }

    override fun observeCountries(): Flow<List<Country>> = countriesFlow

    override suspend fun loadAll(): List<Country> {
        loadAllError?.let { throw it }
        return countriesFlow.value
    }

    override suspend fun search(name: String): List<Country> {
        searchCalls.add(name)
        return countriesFlow.value.filter { it.name.contains(name, ignoreCase = true) }
    }

    override suspend fun getCountryByCode(code: String): Country? =
        getCountryByCodeHandler(code)

    override suspend fun refreshCountries(): SyncResult {
        refreshCalls++
        loadAllError?.let { throw it }
        countriesFlow.value = loadAllData
        lastCacheMillis = currentTimeMillis
        return SyncResult(loadAllData.size, currentTimeMillis)
    }

    override suspend fun oldestCacheMillis(): Long? = lastCacheMillis

    override fun observeFavouriteCountries(): Flow<List<Country>> = favouritesFlow

    override suspend fun getFavourites(): List<Country> = favourites.toList()

    override suspend fun addFavourite(country: Country) {
        if (favourites.none { it.code == country.code }) {
            favourites.add(country)
        }
        favouritesFlow.value = favourites.toList()
    }

    override suspend fun removeFavourite(country: Country) {
        favourites.removeAll { it.code == country.code }
        favouritesFlow.value = favourites.toList()
    }

    override suspend fun isFavourite(code: String): Boolean =
        favourites.any { it.code == code }

    override fun observeRecentCountries(limit: Int): Flow<List<RecentCountry>> = recentFlow

    override suspend fun recordRecentCountry(country: Country) {
        val updated = listOf(RecentCountry(country, currentTimeMillis)) +
            recentFlow.value.filterNot { it.country.code == country.code }
        recentFlow.value = updated
    }

    override suspend fun clearRecentCountries() {
        recentFlow.value = emptyList()
    }

    override fun observeCountryNotes(): Flow<List<CountryNote>> = notesFlow

    override fun observeCountryNote(code: String): Flow<CountryNote?> =
        MutableStateFlow(notesFlow.value.firstOrNull { it.countryCode == code })

    override suspend fun saveCountryNote(country: Country, status: TravelStatus, note: String) {
        val countryNote = CountryNote(
            countryCode = country.code,
            countryName = country.name,
            region = country.region,
            population = country.population,
            area = country.area,
            flagUrl = country.flagUrl,
            status = status,
            note = note.trim(),
            updatedAtMillis = currentTimeMillis
        )
        notesFlow.value = listOf(countryNote) +
            notesFlow.value.filterNot { it.countryCode == country.code }
    }

    override suspend fun deleteCountryNote(code: String) {
        notesFlow.value = notesFlow.value.filterNot { it.countryCode == code }
    }
}
