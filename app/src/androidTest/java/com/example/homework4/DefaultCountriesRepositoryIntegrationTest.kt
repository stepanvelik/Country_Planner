package com.example.homework4

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.homework4.data.local.AppDatabase
import com.example.homework4.data.model.CountryDto
import com.example.homework4.data.model.FlagsDto
import com.example.homework4.data.model.NameDto
import com.example.homework4.data.model.PopularCountries
import com.example.homework4.data.repository.DefaultCountriesRepository
import com.example.homework4.fakes.FakeCountriesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultCountriesRepositoryIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var api: FakeCountriesApi
    private lateinit var repo: DefaultCountriesRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        api = FakeCountriesApi()
        repo = DefaultCountriesRepository(
            api = api,
            favouritesDao = db.favouritesDao(),
            cachedCountriesDao = db.cachedCountriesDao(),
            recentCountriesDao = db.recentCountriesDao(),
            countryNotesDao = db.countryNotesDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addFavouriteTwice_readsSingleRowWithoutDuplicates() = runBlocking {
        val country = TestCountries.alpha
        repo.addFavourite(country)
        repo.addFavourite(country)
        val list = repo.getFavourites()
        assertEquals(1, list.size)
        assertEquals(country.code, list[0].code)
    }

    @Test
    fun writeFavouriteReadBack_preservesFields() = runBlocking {
        val country = TestCountries.beta
        repo.addFavourite(country)
        val loaded = repo.getFavourites().single()
        assertEquals(country.population, loaded.population)
        assertEquals(country.area, loaded.area, 0.0)
        assertEquals(country.flagUrl, loaded.flagUrl)
    }

    @Test
    fun loadAll_viaRetrofitMapping_returnsDomainModels() = runBlocking {
        api.countries = listOf(
            CountryDto(
                cca2 = "ZZ",
                name = NameDto("Zed"),
                capital = listOf("Z-Cap"),
                region = "Z",
                population = 42L,
                area = 12.0,
                flags = FlagsDto("https://z.png")
            )
        )
        val list = repo.loadAll()
        assertEquals(1, list.size)
        assertEquals("ZZ", list[0].code)
        assertEquals("Zed", list[0].name)
    }

    @Test
    fun loadAll_afterRefreshReadsCachedDataWhenNetworkFails() = runBlocking {
        api.countries = listOf(
            CountryDto(
                cca2 = "CA",
                name = NameDto("Cacheland"),
                capital = listOf("Cache City"),
                region = "Test",
                population = 7L,
                area = 14.0,
                flags = FlagsDto("https://cache.png")
            )
        )
        repo.refreshCountries()
        api.failAllRequests = true

        val list = repo.loadAll()

        assertEquals(1, list.size)
        assertEquals("CA", list.single().code)
    }

    @Test
    fun refreshCountries_whenNetworkFailsAndCacheIsEmpty_usesPopularCountries() = runBlocking {
        api.failAllRequests = true

        val result = repo.refreshCountries()
        val list = repo.loadAll()

        assertEquals(PopularCountries.list.size, result.cachedCountries)
        assertEquals(PopularCountries.list.first().code, list.first().code)
    }
}
