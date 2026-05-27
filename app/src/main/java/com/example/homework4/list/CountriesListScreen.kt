package com.example.homework4.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.homework4.R
import com.example.homework4.components.CountryItem
import com.example.homework4.data.model.label
import com.example.homework4.data.preferences.ListFilterMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesListScreen(
    onCountryClick: (String) -> Unit,
    onFavouritesClick: () -> Unit,
    onRecentClick: () -> Unit,
    onSettingsClick: () -> Unit,
    vm: CountriesListViewModel
) {
    val listState by vm.listUiState.collectAsState()
    val favourites by vm.favourites.collectAsState()
    val notes by vm.countryNotes.collectAsState()
    val filterMode by vm.userFilterMode.collectAsState()
    val sortMode by vm.sortMode.collectAsState()
    val sortDirection by vm.sortDirection.collectAsState()
    val regionFilter by vm.regionFilter.collectAsState()
    val availableRegions by vm.availableRegions.collectAsState()
    val isRefreshing by vm.isRefreshing.collectAsState()
    val syncMessage by vm.syncMessage.collectAsState()

    var query by remember { mutableStateOf("") }
    val notesByCountry = notes.associateBy { it.countryCode }

    Column {
        TopAppBar(
            title = { Text(stringResource(R.string.countries_title)) },
            actions = {
                IconButton(onClick = { vm.refresh() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                }

                IconButton(onClick = onRecentClick) {
                    Icon(Icons.Default.History, contentDescription = "История")
                }

                IconButton(onClick = onFavouritesClick) {
                    Icon(Icons.Default.Favorite, contentDescription = "Избранное")
                }

                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Настройки")
                }
            }
        )

        OutlinedTextField(
            value = query,
            onValueChange = { newValue ->
                query = newValue
                vm.onSearchQueryChange(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text(stringResource(R.string.search_country_hint)) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterMode == ListFilterMode.ALL,
                onClick = { vm.setListFilterMode(ListFilterMode.ALL) },
                label = { Text(stringResource(R.string.filter_all)) },
                modifier = Modifier.semantics { role = Role.RadioButton }
            )
            FilterChip(
                selected = filterMode == ListFilterMode.FAVOURITES_ONLY,
                onClick = { vm.setListFilterMode(ListFilterMode.FAVOURITES_ONLY) },
                label = { Text(stringResource(R.string.filter_favourites_only)) },
                modifier = Modifier.semantics { role = Role.RadioButton }
            )
        }

        CountrySortControls(
            sortMode = sortMode,
            sortDirection = sortDirection,
            regionFilter = regionFilter,
            availableRegions = availableRegions,
            onSortModeChange = vm::setSortMode,
            onToggleSortDirection = vm::toggleSortDirection,
            onRegionFilterChange = vm::setRegionFilter
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (!syncMessage.isNullOrBlank()) {
            Text(
                text = syncMessage.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        when (val state = listState) {
            CountriesUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }

            CountriesUiState.Empty -> {
                Text(
                    stringResource(R.string.nothing_found),
                    modifier = Modifier.padding(16.dp)
                )
            }

            is CountriesUiState.Error -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(state.message)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { vm.refresh() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }

            is CountriesUiState.Success -> {
                LazyColumn {
                    items(state.list) { country ->
                        CountryItem(
                            country = country,
                            isFavourite = favourites.any { it.code == country.code },
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

