package com.example.homework4.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun CountrySortControls(
    sortMode: CountrySortMode,
    sortDirection: SortDirection,
    regionFilter: String?,
    availableRegions: List<String>,
    onSortModeChange: (CountrySortMode) -> Unit,
    onToggleSortDirection: () -> Unit,
    onRegionFilterChange: (String?) -> Unit
) {
    Text(
        text = "Сортировка",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .weight(1f)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CountrySortMode.values().toList()) { mode ->
                FilterChip(
                    selected = sortMode == mode,
                    onClick = { onSortModeChange(mode) },
                    label = { Text(mode.label()) },
                    modifier = Modifier.semantics { role = Role.RadioButton }
                )
            }
        }

        IconButton(onClick = onToggleSortDirection) {
            Icon(
                imageVector = if (sortDirection == SortDirection.ASCENDING) {
                    Icons.Default.ArrowUpward
                } else {
                    Icons.Default.ArrowDownward
                },
                contentDescription = if (sortDirection == SortDirection.ASCENDING) {
                    "По возрастанию"
                } else {
                    "По убыванию"
                }
            )
        }
    }

    if (availableRegions.isNotEmpty()) {
        Text(
            text = "Регион",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = regionFilter == null,
                    onClick = { onRegionFilterChange(null) },
                    label = { Text("Все") },
                    modifier = Modifier.semantics { role = Role.RadioButton }
                )
            }
            items(availableRegions) { region ->
                FilterChip(
                    selected = regionFilter == region,
                    onClick = { onRegionFilterChange(region) },
                    label = { Text(region) },
                    modifier = Modifier.semantics { role = Role.RadioButton }
                )
            }
        }
    }
}

