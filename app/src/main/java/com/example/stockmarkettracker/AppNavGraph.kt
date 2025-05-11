package com.example.stockmarkettracker

import SearchPage
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH,
        modifier = modifier // ← this enables proper padding/layout from Scaffold
    ) {
        composable(Routes.SEARCH) {
            SearchPage(navController = navController, modifier = modifier)
        }
        composable(Routes.WATCHLIST) {
            WatchlistScreen(navController)
        }
        composable(Routes.ALERTS) {
            AlertsScreen(navController)
        }
    }
}
