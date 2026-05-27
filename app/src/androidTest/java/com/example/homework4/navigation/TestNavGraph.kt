package com.example.homework4.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homework4.detail.CountryDetailScreen
import com.example.homework4.favourites.FavouritesScreen
import com.example.homework4.list.CountriesListScreen
import com.example.homework4.list.CountriesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestNavGraph(vm: CountriesListViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "list"
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
        composable("detail/{code}") { backStack ->
            CountryDetailScreen(
                code = backStack.arguments?.getString("code").orEmpty(),
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
    }
}
