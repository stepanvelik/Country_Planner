package com.example.homework4.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.homework4.data.userPreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Singleton
class DefaultUserPreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
) : UserPreferencesRepository {

    private val dataStore = context.userPreferencesDataStore

    override val listFilterMode: Flow<ListFilterMode> =
        dataStore.data
            .map { prefs ->
                when (prefs[KEY_LIST_FILTER]) {
                    KEY_FAVOURITES_ONLY -> ListFilterMode.FAVOURITES_ONLY
                    else -> ListFilterMode.ALL
                }
            }
            .distinctUntilChanged()

    override val hideVisitedCountries: Flow<Boolean> =
        dataStore.data
            .map { prefs -> prefs[KEY_HIDE_VISITED] ?: false }
            .distinctUntilChanged()

    override val cacheTtlHours: Flow<Int> =
        dataStore.data
            .map { prefs -> prefs[KEY_CACHE_TTL_HOURS] ?: DEFAULT_CACHE_TTL_HOURS }
            .distinctUntilChanged()

    override val backgroundSyncEnabled: Flow<Boolean> =
        dataStore.data
            .map { prefs -> prefs[KEY_BACKGROUND_SYNC] ?: true }
            .distinctUntilChanged()

    override suspend fun setListFilterMode(mode: ListFilterMode) {
        dataStore.edit { prefs ->
            prefs[KEY_LIST_FILTER] = when (mode) {
                ListFilterMode.ALL -> KEY_ALL
                ListFilterMode.FAVOURITES_ONLY -> KEY_FAVOURITES_ONLY
            }
        }
    }

    override suspend fun setHideVisitedCountries(hide: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_HIDE_VISITED] = hide
        }
    }

    override suspend fun setCacheTtlHours(hours: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_CACHE_TTL_HOURS] = hours.coerceIn(1, 72)
        }
    }

    override suspend fun setBackgroundSyncEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_BACKGROUND_SYNC] = enabled
        }
    }

    companion object {
        private val KEY_LIST_FILTER = stringPreferencesKey("list_filter")
        private val KEY_HIDE_VISITED = booleanPreferencesKey("hide_visited_countries")
        private val KEY_CACHE_TTL_HOURS = intPreferencesKey("cache_ttl_hours")
        private val KEY_BACKGROUND_SYNC = booleanPreferencesKey("background_sync_enabled")
        private const val KEY_ALL = "ALL"
        private const val KEY_FAVOURITES_ONLY = "FAVOURITES_ONLY"
        private const val DEFAULT_CACHE_TTL_HOURS = 24
    }
}
