package com.example.homework4.data.repository

import com.example.homework4.data.local.CachedCountriesDao
import com.example.homework4.data.local.CachedCountryEntity
import com.example.homework4.data.local.CountryNoteEntity
import com.example.homework4.data.local.CountryNotesDao
import com.example.homework4.data.local.FavouriteCountryEntity
import com.example.homework4.data.local.FavouritesDao
import com.example.homework4.data.local.RecentCountriesDao
import com.example.homework4.data.local.RecentCountryEntity
import com.example.homework4.data.model.Country
import com.example.homework4.data.model.CountryNote
import com.example.homework4.data.model.PopularCountries
import com.example.homework4.data.model.RecentCountry
import com.example.homework4.data.model.TravelStatus
import com.example.homework4.data.model.toDomain
import com.example.homework4.data.remote.CountriesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCountriesRepository @Inject constructor(
    private val api: CountriesApi,
    private val favouritesDao: FavouritesDao,
    private val cachedCountriesDao: CachedCountriesDao,
    private val recentCountriesDao: RecentCountriesDao,
    private val countryNotesDao: CountryNotesDao
) : CountriesRepository {

    override fun observeCountries(): Flow<List<Country>> =
        cachedCountriesDao.observeAll().map { entities ->
            entities.map { it.toCountry() }
        }

    override suspend fun loadAll(): List<Country> =
        withContext(Dispatchers.IO) {
            val cached = cachedCountriesDao.getAll().map { it.toCountry() }
            cached.ifEmpty {
                refreshCountries()
                cachedCountriesDao.getAll().map { it.toCountry() }
            }
        }

    override suspend fun search(name: String): List<Country> =
        withContext(Dispatchers.IO) {
            val cached = cachedCountriesDao.search(name).map { it.toCountry() }
            cached.ifEmpty {
                api.searchCountries(name).map { it.toDomain() }
            }
        }

    override suspend fun getCountryByCode(code: String): Country? =
        withContext(Dispatchers.IO) {
            cachedCountriesDao.getByCode(code)?.toCountry()
                ?: api.getCountryByCode(code).firstOrNull()?.toDomain()?.also { country ->
                    val now = System.currentTimeMillis()
                    cachedCountriesDao.insertAll(listOf(CachedCountryEntity.fromCountry(country, now)))
                }
        }

    override suspend fun refreshCountries(): SyncResult =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val countries = runCatching {
                api.getCountries().map { it.toDomain() }
            }.getOrElse { error ->
                val hasCache = cachedCountriesDao.count() > 0
                if (hasCache) throw error
                PopularCountries.list
            }
            cachedCountriesDao.replaceAll(
                countries.map { CachedCountryEntity.fromCountry(it, now) }
            )
            SyncResult(cachedCountries = countries.size, syncedAtMillis = now)
        }

    override suspend fun oldestCacheMillis(): Long? =
        withContext(Dispatchers.IO) {
            cachedCountriesDao.oldestCacheMillis()
        }

    override fun observeFavouriteCountries(): Flow<List<Country>> =
        favouritesDao.observeAll().map { entities ->
            entities.map { it.toCountry() }
        }

    override suspend fun getFavourites(): List<Country> =
        withContext(Dispatchers.IO) {
            favouritesDao.getAll().map { it.toCountry() }
        }

    override suspend fun addFavourite(country: Country) =
        withContext(Dispatchers.IO) {
            favouritesDao.insert(FavouriteCountryEntity.fromCountry(country))
        }

    override suspend fun removeFavourite(country: Country) =
        withContext(Dispatchers.IO) {
            favouritesDao.delete(FavouriteCountryEntity.fromCountry(country))
        }

    override suspend fun isFavourite(code: String): Boolean =
        withContext(Dispatchers.IO) {
            favouritesDao.getByCode(code) != null
        }

    override fun observeRecentCountries(limit: Int): Flow<List<RecentCountry>> =
        recentCountriesDao.observeRecent(limit).map { entities ->
            entities.map { it.toRecentCountry() }
        }

    override suspend fun recordRecentCountry(country: Country) =
        withContext(Dispatchers.IO) {
            recentCountriesDao.upsert(
                RecentCountryEntity.fromCountry(
                    country = country,
                    viewedAtMillis = System.currentTimeMillis()
                )
            )
        }

    override suspend fun clearRecentCountries() =
        withContext(Dispatchers.IO) {
            recentCountriesDao.clear()
        }

    override fun observeCountryNotes(): Flow<List<CountryNote>> =
        countryNotesDao.observeAll().map { entities ->
            entities.map { it.toCountryNote() }
        }

    override fun observeCountryNote(code: String): Flow<CountryNote?> =
        countryNotesDao.observeByCode(code).map { it?.toCountryNote() }

    override suspend fun saveCountryNote(
        country: Country,
        status: TravelStatus,
        note: String
    ) = withContext(Dispatchers.IO) {
        countryNotesDao.upsert(
            CountryNoteEntity.fromCountry(
                country = country,
                status = status,
                note = note.trim(),
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteCountryNote(code: String) =
        withContext(Dispatchers.IO) {
            countryNotesDao.deleteByCode(code)
        }
}
