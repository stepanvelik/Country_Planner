package com.example.homework4

import com.example.homework4.data.model.TravelStatus
import com.example.homework4.detail.CountryDetailUiState
import com.example.homework4.fakes.FakeCountriesRepository
import com.example.homework4.fakes.FakeCountrySyncScheduler
import com.example.homework4.fakes.FakeUserPreferencesRepository
import com.example.homework4.list.CountrySortMode
import com.example.homework4.list.CountriesListViewModel
import com.example.homework4.list.CountriesUiState
import com.example.homework4.list.SortDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesListViewModelTest {

    @Test
    fun cachedCountries_areShownAsOfflineFirstList() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha)
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()

            val success = vm.listUiState.value as CountriesUiState.Success
            assertEquals(listOf(TestCountries.alpha), success.list)
        }
    }

    @Test
    fun emptyCacheAndRefreshFailure_setsErrorState() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllError = RuntimeException("network")
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()

            val err = vm.listUiState.value as CountriesUiState.Error
            assertTrue(err.message.contains("network"))
        }
    }

    @Test
    fun search_filtersLocalCacheWithoutNetworkCall() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta)
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()
            vm.onSearchQueryChange("beta")
            advanceUntilIdle()

            val success = vm.listUiState.value as CountriesUiState.Success
            assertEquals(listOf(TestCountries.beta), success.list)
            assertTrue(fake.searchCalls.isEmpty())
        }
    }

    @Test
    fun loadCountryByCode_recordsRecentCountry() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta)
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()
            vm.loadCountryByCode("BE")
            advanceUntilIdle()

            val detail = vm.detailState.value as CountryDetailUiState.Success
            assertEquals("BE", detail.country.code)
            assertEquals(listOf("BE"), vm.recentCountries.value.map { it.country.code })
        }
    }

    @Test
    fun refresh_afterLoadError_triggersNewLoadAndSuccess() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllError = RuntimeException("fail")
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()
            assertTrue(vm.listUiState.value is CountriesUiState.Error)

            fake.loadAllError = null
            fake.loadAllData = listOf(TestCountries.alpha)
            vm.refresh()
            advanceUntilIdle()

            assertTrue(vm.listUiState.value is CountriesUiState.Success)
        }
    }

    @Test
    fun visitedStatusAndSetting_hideVisitedCountryFromList() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta)
            }
            val prefs = FakeUserPreferencesRepository()
            val vm = CountriesListViewModel(fake, prefs)

            advanceUntilIdle()
            vm.saveCountryNote(TestCountries.alpha, TravelStatus.VISITED, "Done")
            prefs.setHideVisitedCountries(true)
            advanceUntilIdle()

            val success = vm.listUiState.value as CountriesUiState.Success
            assertEquals(listOf(TestCountries.beta), success.list)
        }
    }

    @Test
    fun sortByPopulation_ordersCountriesByPopulationDescending() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta)
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()
            vm.setSortMode(CountrySortMode.POPULATION)
            vm.setSortDirection(SortDirection.DESCENDING)
            advanceUntilIdle()

            val success = vm.listUiState.value as CountriesUiState.Success
            assertEquals(listOf(TestCountries.beta, TestCountries.alpha), success.list)
        }
    }

    @Test
    fun sortByArea_ordersCountriesByAreaDescending() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta, TestCountries.gamma)
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()
            vm.setSortMode(CountrySortMode.AREA)
            vm.setSortDirection(SortDirection.DESCENDING)
            advanceUntilIdle()

            val success = vm.listUiState.value as CountriesUiState.Success
            assertEquals(
                listOf(TestCountries.gamma, TestCountries.alpha, TestCountries.beta),
                success.list
            )
        }
    }

    @Test
    fun regionFilter_keepsCountriesFromSelectedRegion() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.gamma)
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()
            vm.setRegionFilter("Other")
            advanceUntilIdle()

            val success = vm.listUiState.value as CountriesUiState.Success
            assertEquals(listOf(TestCountries.gamma), success.list)
        }
    }

    @Test
    fun personalCountries_useSamePopulationSorting() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta)
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()
            vm.toggleFavourite(TestCountries.alpha)
            vm.toggleFavourite(TestCountries.beta)
            vm.setSortMode(CountrySortMode.POPULATION)
            vm.setSortDirection(SortDirection.DESCENDING)
            advanceUntilIdle()

            assertEquals(
                listOf("BE", "AL"),
                vm.personalCountries.value.map { it.code }
            )
        }
    }

    @Test
    fun toggleSortDirection_reversesAlphabeticalOrder() = runTest {
        withMainDispatcher {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta, TestCountries.gamma)
            }
            val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

            advanceUntilIdle()
            vm.setSortMode(CountrySortMode.NAME)
            vm.toggleSortDirection()
            advanceUntilIdle()

            val success = vm.listUiState.value as CountriesUiState.Success
            assertEquals(
                listOf(TestCountries.gamma, TestCountries.beta, TestCountries.alpha),
                success.list
            )
        }
    }

    @Test
    fun backgroundSyncSetting_updatesScheduler() = runTest {
        withMainDispatcher {
            val scheduler = FakeCountrySyncScheduler()
            val prefs = FakeUserPreferencesRepository()
            val vm = CountriesListViewModel(
                repository = FakeCountriesRepository(),
                userPreferences = prefs,
                syncScheduler = scheduler
            )

            advanceUntilIdle()
            vm.setBackgroundSyncEnabled(false)
            advanceUntilIdle()

            assertTrue(scheduler.enabledValues.contains(true))
            assertTrue(scheduler.enabledValues.contains(false))
        }
    }

    private suspend fun TestScope.withMainDispatcher(block: suspend () -> Unit) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }
}
