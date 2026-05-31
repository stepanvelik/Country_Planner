package com.example.homework4.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.homework4.list.CountriesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: CountriesListViewModel,
    onBack: () -> Unit
) {
    val hideVisited by vm.hideVisitedCountries.collectAsState()
    val ttlHours by vm.cacheTtlHours.collectAsState()
    val backgroundSyncEnabled by vm.backgroundSyncEnabled.collectAsState()
    val isRefreshing by vm.isRefreshing.collectAsState()
    val syncMessage by vm.syncMessage.collectAsState()
    val ttlOptions = listOf(1, 6, 24)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingSwitchRow(
                title = "Скрывать посещенные",
                checked = hideVisited,
                onCheckedChange = vm::setHideVisitedCountries
            )

            SettingSwitchRow(
                title = "Фоновое обновление",
                checked = backgroundSyncEnabled,
                onCheckedChange = vm::setBackgroundSyncEnabled
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Срок актуальности кэша", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ttlOptions.forEach { hours ->
                        FilterChip(
                            selected = ttlHours == hours,
                            onClick = { vm.setCacheTtlHours(hours) },
                            label = { Text("${hours}ч") },
                            modifier = Modifier.semantics { role = Role.RadioButton }
                        )
                    }
                }
            }

            Button(onClick = { vm.refresh() }) {
                Text("Обновить кэш")
            }

            if (isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (!syncMessage.isNullOrBlank()) {
                Text(
                    text = syncMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
