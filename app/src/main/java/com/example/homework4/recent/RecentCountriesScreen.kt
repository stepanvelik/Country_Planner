package com.example.homework4.recent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentCountriesScreen(
    vm: CountriesListViewModel,
    onCountryClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val recentCountries by vm.recentCountries.collectAsState()
    val favourites by vm.favourites.collectAsState()
    val notes by vm.countryNotes.collectAsState()
    val favouriteCodes = favourites.map { it.code }.toSet()
    val notesByCountry = notes.associateBy { it.countryCode }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (recentCountries.isNotEmpty()) {
                        IconButton(onClick = { vm.clearRecentCountries() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Очистить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (recentCountries.isEmpty()) {
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("История просмотров пока пустая")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) {
                    Text("К списку стран")
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(recentCountries, key = { it.country.code }) { recent ->
                    val country = recent.country
                    CountryItem(
                        country = country,
                        isFavourite = country.code in favouriteCodes,
                        statusLabel = notesByCountry[country.code]?.status?.label(),
                        onClick = { onCountryClick(country.code) },
                        onFavourite = { vm.toggleFavourite(country) }
                    )
                }
            }
        }
    }
}
