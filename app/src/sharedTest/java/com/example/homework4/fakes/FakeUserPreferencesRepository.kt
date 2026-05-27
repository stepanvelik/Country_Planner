package com.example.homework4.fakes

import com.example.homework4.data.preferences.ListFilterMode
import com.example.homework4.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserPreferencesRepository(
    initialMode: ListFilterMode = ListFilterMode.ALL
) : UserPreferencesRepository {

    private val _listFilterMode = MutableStateFlow(initialMode)
    private val _hideVisitedCountries = MutableStateFlow(false)
    private val _cacheTtlHours = MutableStateFlow(24)
    private val _backgroundSyncEnabled = MutableStateFlow(true)

    override val listFilterMode: Flow<ListFilterMode> = _listFilterMode.asStateFlow()
    override val hideVisitedCountries: Flow<Boolean> = _hideVisitedCountries.asStateFlow()
    override val cacheTtlHours: Flow<Int> = _cacheTtlHours.asStateFlow()
    override val backgroundSyncEnabled: Flow<Boolean> = _backgroundSyncEnabled.asStateFlow()

    override suspend fun setListFilterMode(mode: ListFilterMode) {
        _listFilterMode.value = mode
    }

    override suspend fun setHideVisitedCountries(hide: Boolean) {
        _hideVisitedCountries.value = hide
    }

    override suspend fun setCacheTtlHours(hours: Int) {
        _cacheTtlHours.value = hours
    }

    override suspend fun setBackgroundSyncEnabled(enabled: Boolean) {
        _backgroundSyncEnabled.value = enabled
    }

    fun setModeSync(mode: ListFilterMode) {
        _listFilterMode.value = mode
    }

    fun setHideVisitedSync(hide: Boolean) {
        _hideVisitedCountries.value = hide
    }
}
