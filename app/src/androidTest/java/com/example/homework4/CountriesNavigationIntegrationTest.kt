package com.example.homework4

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.homework4.detail.CountryDetailUiState
import com.example.homework4.fakes.FakeCountriesRepository
import com.example.homework4.fakes.FakeUserPreferencesRepository
import com.example.homework4.list.CountriesListViewModel
import com.example.homework4.list.CountriesUiState
import com.example.homework4.navigation.TestNavGraph
import com.example.homework4.ui.theme.Homework4Theme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountriesNavigationIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun list_clickCountry_opensDetailForCorrectCode() {
        val fake = FakeCountriesRepository().apply {
            loadAllData = listOf(TestCountries.alpha)
            getCountryByCodeHandler = { code ->
                TestCountries.alpha.takeIf { it.code == code }
            }
        }
        val vm = CountriesListViewModel(fake, FakeUserPreferencesRepository())

        composeRule.setContent {
            Homework4Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TestNavGraph(vm = vm)
                }
            }
        }

        composeRule.waitUntil(timeoutMillis = 5000) {
            vm.listUiState.value is CountriesUiState.Success
        }
        composeRule.onNodeWithText(TestCountries.alpha.name, substring = true).performClick()
        composeRule.waitUntil(timeoutMillis = 5000) {
            vm.detailState.value is CountryDetailUiState.Success
        }
        val detail = vm.detailState.value as CountryDetailUiState.Success
        assertEquals(TestCountries.alpha.code, detail.country.code)
        composeRule.onNodeWithText("Детали страны").assertIsDisplayed()
    }
}

