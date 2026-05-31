package com.example.homework4.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {

    @Query("SELECT * FROM favourites")
    fun observeAll(): Flow<List<FavouriteCountryEntity>>

    @Query("SELECT * FROM favourites")
    suspend fun getAll(): List<FavouriteCountryEntity>

    @Query("SELECT * FROM favourites WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): FavouriteCountryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(country: FavouriteCountryEntity)

    @Delete
    suspend fun delete(country: FavouriteCountryEntity)
}