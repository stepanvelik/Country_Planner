package com.example.homework4.data.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val listFilterMode: Flow<ListFilterMode>
    val hideVisitedCountries: Flow<Boolean>
    val cacheTtlHours: Flow<Int>
    val backgroundSyncEnabled: Flow<Boolean>

    suspend fun setListFilterMode(mode: ListFilterMode)
    suspend fun setHideVisitedCountries(hide: Boolean)
    suspend fun setCacheTtlHours(hours: Int)
    suspend fun setBackgroundSyncEnabled(enabled: Boolean)
}
