package com.example.homework4.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework4.data.model.Country
import com.example.homework4.data.model.CountryNote
import com.example.homework4.data.model.RecentCountry
import com.example.homework4.data.model.TravelStatus
import com.example.homework4.data.preferences.ListFilterMode
import com.example.homework4.data.preferences.UserPreferencesRepository
import com.example.homework4.data.repository.CountriesRepository
import com.example.homework4.detail.CountryDetailUiState
import com.example.homework4.sync.CacheRefreshPolicy
import com.example.homework4.sync.CountrySyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CountriesListViewModel @Inject constructor(
    private val repository: CountriesRepository,
    private val userPreferences: UserPreferencesRepository,
    private val syncScheduler: CountrySyncScheduler = NoOpCountrySyncScheduler,
    private val cacheRefreshPolicy: CacheRefreshPolicy = CacheRefreshPolicy()
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val sortModeInternal = MutableStateFlow(CountrySortMode.NAME)
    private val sortDirectionInternal = MutableStateFlow(SortDirection.ASCENDING)
    private val regionFilterInternal = MutableStateFlow<String?>(null)
    private val isRefreshingInternal = MutableStateFlow(false)
    private val loadError = MutableStateFlow<String?>(null)
    private val syncMessageInternal = MutableStateFlow<String?>(null)

    private val listPreferences = combine(
        userPreferences.listFilterMode,
        userPreferences.hideVisitedCountries
    ) { filterMode, hideVisited ->
        ListPreferences(filterMode = filterMode, hideVisited = hideVisited)
    }

    private val listData = combine(
        repository.observeCountries(),
        repository.observeFavouriteCountries(),
        repository.observeCountryNotes()
    ) { countries, favourites, notes ->
        ListData(
            countries = countries,
            favouriteCodes = favourites.map { it.code }.toSet(),
            notesByCountry = notes.associateBy { it.countryCode }
        )
    }

    private val listControls = combine(
        searchQuery,
        sortModeInternal,
        sortDirectionInternal,
        regionFilterInternal
    ) { query, sortMode, sortDirection, regionFilter ->
        ListControls(
            query = query,
            sortMode = sortMode,
            sortDirection = sortDirection,
            regionFilter = regionFilter
        )
    }

    private val listLoadingState = combine(
        isRefreshingInternal,
        loadError
    ) { isRefreshing, errorMessage ->
        ListLoadingState(
            isRefreshing = isRefreshing,
            errorMessage = errorMessage
        )
    }

    val listUiState: StateFlow<CountriesUiState> = combine(
        listData,
        listPreferences,
        listControls,
        listLoadingState
    ) { data, preferences, controls, loadingState ->
        buildListState(
            data = data,
            preferences = preferences,
            query = controls.query,
            sortMode = controls.sortMode,
            sortDirection = controls.sortDirection,
            regionFilter = controls.regionFilter,
            isRefreshing = loadingState.isRefreshing,
            errorMessage = loadingState.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CountriesUiState.Loading
    )

    val favourites: StateFlow<List<Country>> =
        repository.observeFavouriteCountries().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val sortMode: StateFlow<CountrySortMode> = sortModeInternal.asStateFlow()
    val sortDirection: StateFlow<SortDirection> = sortDirectionInternal.asStateFlow()
    val regionFilter: StateFlow<String?> = regionFilterInternal.asStateFlow()

    val availableRegions: StateFlow<List<String>> =
        repository.observeCountries()
            .map { countries ->
                countries.map { it.region }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    val recentCountries: StateFlow<List<RecentCountry>> =
        repository.observeRecentCountries().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val countryNotes: StateFlow<List<CountryNote>> =
        repository.observeCountryNotes().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val personalCountries: StateFlow<List<Country>> = combine(
        repository.observeFavouriteCountries(),
        sortModeInternal,
        sortDirectionInternal,
        regionFilterInternal
    ) { countries, sortMode, sortDirection, regionFilter ->
        val filtered = if (regionFilter == null) {
            countries
        } else {
            countries.filter { it.region == regionFilter }
        }
        sortCountries(filtered, sortMode, sortDirection)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val userFilterMode: StateFlow<ListFilterMode> =
        userPreferences.listFilterMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ListFilterMode.ALL
        )

    val hideVisitedCountries: StateFlow<Boolean> =
        userPreferences.hideVisitedCountries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val cacheTtlHours: StateFlow<Int> =
        userPreferences.cacheTtlHours.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 24
        )

    val backgroundSyncEnabled: StateFlow<Boolean> =
        userPreferences.backgroundSyncEnabled.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    val isRefreshing: StateFlow<Boolean> = isRefreshingInternal.asStateFlow()
    val syncMessage: StateFlow<String?> = syncMessageInternal.asStateFlow()

    private val _detailState =
        MutableStateFlow<CountryDetailUiState>(CountryDetailUiState.Loading)
    val detailState: StateFlow<CountryDetailUiState> = _detailState.asStateFlow()

    val detailCountryNote: StateFlow<CountryNote?> = combine(
        detailState,
        repository.observeCountryNotes()
    ) { detail, notes ->
        val code = (detail as? CountryDetailUiState.Success)?.country?.code
        notes.firstOrNull { it.countryCode == code }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val isDetailCountryFavourite: StateFlow<Boolean> = combine(
        detailState,
        repository.observeFavouriteCountries()
    ) { detail, favouriteCountries ->
        val codes = favouriteCountries.map { it.code }.toSet()
        when (detail) {
            is CountryDetailUiState.Success -> detail.country.code in codes
            else -> false
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    private var detailJob: Job? = null
    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            userPreferences.backgroundSyncEnabled.collect { enabled ->
                syncScheduler.setBackgroundSyncEnabled(enabled)
            }
        }
        refreshIfNeeded()
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.update { query }
    }

    fun setSortMode(mode: CountrySortMode) {
        sortModeInternal.value = mode
    }

    fun toggleSortDirection() {
        sortDirectionInternal.value = sortDirectionInternal.value.next()
    }

    fun setSortDirection(direction: SortDirection) {
        sortDirectionInternal.value = direction
    }

    fun setRegionFilter(region: String?) {
        regionFilterInternal.value = region
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            refreshCountries()
        }
    }

    fun setListFilterMode(mode: ListFilterMode) {
        viewModelScope.launch {
            userPreferences.setListFilterMode(mode)
        }
    }

    fun setHideVisitedCountries(hide: Boolean) {
        viewModelScope.launch {
            userPreferences.setHideVisitedCountries(hide)
        }
    }

    fun setCacheTtlHours(hours: Int) {
        viewModelScope.launch {
            userPreferences.setCacheTtlHours(hours)
            refreshIfNeeded()
        }
    }

    fun setBackgroundSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setBackgroundSyncEnabled(enabled)
            syncScheduler.setBackgroundSyncEnabled(enabled)
        }
    }

    fun toggleFavourite(country: Country) {
        viewModelScope.launch {
            val isFavourite = favourites.value.any { it.code == country.code }
            if (isFavourite) {
                repository.removeFavourite(country)
            } else {
                repository.addFavourite(country)
            }
        }
    }

    fun loadCountryByCode(code: String) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            _detailState.value = CountryDetailUiState.Loading
            try {
                val country = repository.getCountryByCode(code)
                _detailState.value =
                    if (country == null) {
                        CountryDetailUiState.Empty
                    } else {
                        repository.recordRecentCountry(country)
                        CountryDetailUiState.Success(country)
                    }
            } catch (e: Exception) {
                _detailState.value =
                    CountryDetailUiState.Error(
                        "Не удалось загрузить страну: ${e.message ?: "неизвестная ошибка"}"
                    )
            }
        }
    }

    fun saveCountryNote(country: Country, status: TravelStatus, note: String) {
        viewModelScope.launch {
            repository.saveCountryNote(country, status, note)
        }
    }

    fun deleteCountryNote(code: String) {
        viewModelScope.launch {
            repository.deleteCountryNote(code)
        }
    }

    fun clearRecentCountries() {
        viewModelScope.launch {
            repository.clearRecentCountries()
        }
    }

    private fun refreshIfNeeded() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val ttlHours = userPreferences.cacheTtlHours.first()
            val shouldRefresh = cacheRefreshPolicy.shouldRefresh(
                oldestCacheMillis = repository.oldestCacheMillis(),
                nowMillis = System.currentTimeMillis(),
                ttlHours = ttlHours
            )
            if (shouldRefresh) {
                refreshCountries()
            }
        }
    }

    private suspend fun refreshCountries() {
        isRefreshingInternal.value = true
        loadError.value = null
        try {
            val result = repository.refreshCountries()
            syncMessageInternal.value =
                "Кэш обновлен: ${result.cachedCountries} стран"
        } catch (e: Exception) {
            loadError.value = e.message ?: "неизвестная ошибка"
            syncMessageInternal.value =
                "Не удалось обновить кэш, показываются локальные данные"
        } finally {
            isRefreshingInternal.value = false
        }
    }

    private fun buildListState(
        data: ListData,
        preferences: ListPreferences,
        query: String,
        sortMode: CountrySortMode,
        sortDirection: SortDirection,
        regionFilter: String?,
        isRefreshing: Boolean,
        errorMessage: String?
    ): CountriesUiState {
        val normalizedQuery = query.trim()
        val searched = if (normalizedQuery.isBlank()) {
            data.countries
        } else {
            data.countries.filter { country ->
                country.name.contains(normalizedQuery, ignoreCase = true) ||
                    country.capital.contains(normalizedQuery, ignoreCase = true) ||
                    country.region.contains(normalizedQuery, ignoreCase = true)
            }
        }

        val filteredByMode = when (preferences.filterMode) {
            ListFilterMode.ALL -> searched
            ListFilterMode.FAVOURITES_ONLY ->
                searched.filter { it.code in data.favouriteCodes }
        }

        val filteredByVisited = if (preferences.hideVisited) {
            filteredByMode.filter { country ->
                data.notesByCountry[country.code]?.status != TravelStatus.VISITED
            }
        } else {
            filteredByMode
        }

        val filteredByRegion = if (regionFilter == null) {
            filteredByVisited
        } else {
            filteredByVisited.filter { it.region == regionFilter }
        }

        val sorted = sortCountries(filteredByRegion, sortMode, sortDirection)

        return when {
            data.countries.isEmpty() && (isRefreshing || errorMessage == null) ->
                CountriesUiState.Loading
            data.countries.isEmpty() && errorMessage != null ->
                CountriesUiState.Error("Нет локального кэша: $errorMessage")
            sorted.isEmpty() -> CountriesUiState.Empty
            else -> CountriesUiState.Success(sorted)
        }
    }

    private fun sortCountries(
        countries: List<Country>,
        sortMode: CountrySortMode,
        sortDirection: SortDirection
    ): List<Country> =
        when (sortMode) {
            CountrySortMode.NAME ->
                if (sortDirection == SortDirection.ASCENDING) {
                    countries.sortedBy { it.name }
                } else {
                    countries.sortedByDescending { it.name }
                }
            CountrySortMode.POPULATION ->
                if (sortDirection == SortDirection.ASCENDING) {
                    countries.sortedBy { it.population }
                } else {
                    countries.sortedByDescending { it.population }
                }
            CountrySortMode.AREA ->
                if (sortDirection == SortDirection.ASCENDING) {
                    countries.sortedBy { it.area }
                } else {
                    countries.sortedByDescending { it.area }
                }
        }

    private data class ListData(
        val countries: List<Country>,
        val favouriteCodes: Set<String>,
        val notesByCountry: Map<String, CountryNote>
    )

    private data class ListPreferences(
        val filterMode: ListFilterMode,
        val hideVisited: Boolean
    )

    private data class ListControls(
        val query: String,
        val sortMode: CountrySortMode,
        val sortDirection: SortDirection,
        val regionFilter: String?
    )

    private data class ListLoadingState(
        val isRefreshing: Boolean,
        val errorMessage: String?
    )

    private object NoOpCountrySyncScheduler : CountrySyncScheduler {
        override fun setBackgroundSyncEnabled(enabled: Boolean) = Unit
    }
}
