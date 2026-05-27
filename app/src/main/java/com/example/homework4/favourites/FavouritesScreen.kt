package com.example.homework4.favourites

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.homework4.components.CountryItem
import com.example.homework4.data.model.label
import com.example.homework4.list.CountriesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    vm: CountriesListViewModel,
    onCountryClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val favourites by vm.favourites.collectAsState()
    val notes by vm.countryNotes.collectAsState()
    val notesByCountry = notes.associateBy { it.countryCode }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (favourites.isEmpty()) {
            Text(
                text = "Избранных стран пока нет",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(favourites, key = { it.code }) { country ->
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
