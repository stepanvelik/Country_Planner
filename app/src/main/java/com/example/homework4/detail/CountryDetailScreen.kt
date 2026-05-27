package com.example.homework4.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.homework4.data.model.Country
import com.example.homework4.data.model.TravelStatus
import com.example.homework4.data.model.label
import com.example.homework4.list.CountriesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    code: String,
    vm: CountriesListViewModel,
    onBack: () -> Unit
) {
    val detailState by vm.detailState.collectAsState()
    val isFavourite by vm.isDetailCountryFavourite.collectAsState()
    val countryNote by vm.detailCountryNote.collectAsState()

    LaunchedEffect(code) {
        if (code.isNotBlank()) {
            vm.loadCountryByCode(code)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали страны") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    when (val state = detailState) {
                        is CountryDetailUiState.Success -> {
                            IconButton(
                                onClick = { vm.toggleFavourite(state.country) }
                            ) {
                                Icon(
                                    imageVector = if (isFavourite) {
                                        Icons.Default.Favorite
                                    } else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = "Избранное"
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            )
        }
    ) { padding ->
        when (val state = detailState) {
            CountryDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            CountryDetailUiState.Empty -> {
                Text(
                    text = "Страна не найдена",
                    modifier = Modifier.padding(padding)
                )
            }

            is CountryDetailUiState.Error -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(state.message)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { vm.loadCountryByCode(code) }) {
                        Text("Повторить")
                    }
                }
            }

            is CountryDetailUiState.Success -> {
                CountryDetailContent(
                    country = state.country,
                    currentNoteStatus = countryNote?.status,
                    currentNoteText = countryNote?.note.orEmpty(),
                    currentNoteUpdatedAt = countryNote?.updatedAtMillis,
                    hasSavedNote = countryNote != null,
                    padding = padding,
                    onSaveNote = { status, note ->
                        vm.saveCountryNote(state.country, status, note)
                    },
                    onDeleteNote = {
                        vm.deleteCountryNote(state.country.code)
                    }
                )
            }
        }
    }
}

@Composable
private fun CountryDetailContent(
    country: Country,
    currentNoteStatus: TravelStatus?,
    currentNoteText: String,
    currentNoteUpdatedAt: Long?,
    hasSavedNote: Boolean,
    padding: PaddingValues,
    onSaveNote: (TravelStatus, String) -> Unit,
    onDeleteNote: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf(TravelStatus.WANT_TO_VISIT) }
    var noteText by remember { mutableStateOf("") }

    LaunchedEffect(country.code, currentNoteUpdatedAt) {
        selectedStatus = currentNoteStatus ?: TravelStatus.WANT_TO_VISIT
        noteText = currentNoteText
    }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Image(
                painter = rememberAsyncImagePainter(country.flagUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = country.name,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Регион: ${country.region}")
                    Text("Столица: ${country.capital}")
                    Text("Население: ${country.population}")
                    Text("Площадь: ${country.area.toLong()} км²")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Личная запись",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TravelStatus.values().forEach { status ->
                            FilterChip(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status },
                                label = { Text(status.label()) },
                                modifier = Modifier.semantics {
                                    role = Role.RadioButton
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text("Заметка") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onSaveNote(selectedStatus, noteText) }) {
                            Text("Сохранить")
                        }

                        if (hasSavedNote) {
                            TextButton(onClick = onDeleteNote) {
                                Text("Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}
