package com.example.homework4.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = "list",
        label = "Поиск",
        icon = Icons.Default.Search
    ),
    BottomNavItem(
        route = "personal",
        label = "Мои страны",
        icon = Icons.Default.Home
    )
)

