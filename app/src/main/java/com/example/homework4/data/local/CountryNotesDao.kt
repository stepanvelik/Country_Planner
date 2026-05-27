package com.example.homework4.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryNotesDao {

    @Query("SELECT * FROM country_notes ORDER BY updatedAtMillis DESC")
    fun observeAll(): Flow<List<CountryNoteEntity>>

    @Query("SELECT * FROM country_notes WHERE countryCode = :code LIMIT 1")
    fun observeByCode(code: String): Flow<CountryNoteEntity?>

    @Query("SELECT * FROM country_notes WHERE countryCode = :code LIMIT 1")
    suspend fun getByCode(code: String): CountryNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: CountryNoteEntity)

    @Query("DELETE FROM country_notes WHERE countryCode = :code")
    suspend fun deleteByCode(code: String)
}

