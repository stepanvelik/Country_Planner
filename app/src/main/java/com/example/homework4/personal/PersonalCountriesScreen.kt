package com.example.homework4.personal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homework4.components.CountryItem
import com.example.homework4.data.model.label
import com.example.homework4.list.CountriesListViewModel
import com.example.homework4.list.CountrySortControls

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalCountriesScreen(
    vm: CountriesListViewModel,
    onCountryClick: (String) -> Unit
) {
    val countries by vm.personalCountries.collectAsState()
    val notes by vm.countryNotes.collectAsState()
    val sortMode by vm.sortMode.collectAsState()
    val sortDirection by vm.sortDirection.collectAsState()
    val regionFilter by vm.regionFilter.collectAsState()
    val availableRegions by vm.availableRegions.collectAsState()
    val notesByCountry = notes.associateBy { it.countryCode }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мои страны") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CountrySortControls(
                sortMode = sortMode,
                sortDirection = sortDirection,
                regionFilter = regionFilter,
                availableRegions = availableRegions,
                onSortModeChange = vm::setSortMode,
                onToggleSortDirection = vm::toggleSortDirection,
                onRegionFilterChange = vm::setRegionFilter
            )

            if (countries.isEmpty()) {
                Text(
                    text = "Избранных стран пока нет",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(countries, key = { it.code }) { country ->
                        CountryItem(
                            country = country,
                            isFavourite = true,
                            statusLabel = notesByCountry[country.code]?.status?.label(),
                            onClick = { onCountryClick(country.code) },
                            onFavourite = { vm.toggleFavourite(country) }
                        )
                    }
                }
            }
        }
    }
}

