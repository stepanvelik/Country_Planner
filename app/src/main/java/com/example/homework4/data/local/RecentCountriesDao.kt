package com.example.homework4.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentCountriesDao {

    @Query("SELECT * FROM recent_countries ORDER BY viewedAtMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<RecentCountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(country: RecentCountryEntity)

    @Query("DELETE FROM recent_countries")
    suspend fun clear()
}

