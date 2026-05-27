package com.example.homework4.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedCountriesDao {

    @Query("SELECT * FROM country_cache ORDER BY name")
    fun observeAll(): Flow<List<CachedCountryEntity>>

    @Query("SELECT * FROM country_cache ORDER BY name")
    suspend fun getAll(): List<CachedCountryEntity>

    @Query(
        """
        SELECT * FROM country_cache
        WHERE name LIKE '%' || :query || '%'
           OR capital LIKE '%' || :query || '%'
           OR region LIKE '%' || :query || '%'
        ORDER BY name
        """
    )
    suspend fun search(query: String): List<CachedCountryEntity>

    @Query("SELECT * FROM country_cache WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): CachedCountryEntity?

    @Query("SELECT MIN(cachedAtMillis) FROM country_cache")
    suspend fun oldestCacheMillis(): Long?

    @Query("SELECT COUNT(*) FROM country_cache")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CachedCountryEntity>)

    @Query("DELETE FROM country_cache")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(countries: List<CachedCountryEntity>) {
        clear()
        insertAll(countries)
    }
}

