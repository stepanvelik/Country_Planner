package com.example.homework4.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.homework4.detail.CountryDetailScreen
import com.example.homework4.favourites.FavouritesScreen
import com.example.homework4.list.CountriesListScreen
import com.example.homework4.list.CountriesListViewModel
import com.example.homework4.personal.PersonalCountriesScreen
import com.example.homework4.recent.RecentCountriesScreen
import com.example.homework4.settings.SettingsScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val vm: CountriesListViewModel = hiltViewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "list",
            modifier = Modifier.padding(padding)
        ) {
            composable("list") {
                CountriesListScreen(
                    vm = vm,
                    onCountryClick = { code ->
                        navController.navigate("detail/$code")
                    },
                    onFavouritesClick = {
                        navController.navigate("favourites")
                    },
                    onRecentClick = {
                        navController.navigate("recent")
                    },
                    onSettingsClick = {
                        navController.navigate("settings")
                    }
                )
            }

            composable("personal") {
                PersonalCountriesScreen(
                    vm = vm,
                    onCountryClick = { code ->
                        navController.navigate("detail/$code")
                    }
                )
            }

            composable("detail/{code}") { backStack ->
                CountryDetailScreen(
                    code = backStack.arguments?.getString("code") ?: "",
                    vm = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("favourites") {
                FavouritesScreen(
                    vm = vm,
                    onCountryClick = { code ->
                        navController.navigate("detail/$code")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("recent") {
                RecentCountriesScreen(
                    vm = vm,
                    onCountryClick = { code ->
                        navController.navigate("detail/$code")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

