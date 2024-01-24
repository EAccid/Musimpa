package com.eaccid.musimpa.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eaccid.musimpa.ui.mainscreen.MainScreen
import com.eaccid.musimpa.ui.movieslist.ui.theme.MoviesScreen

@Composable
fun ScreenNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.MoviesScreen.route) {
            MoviesScreen(navController = navController)
        }
        composable(route = Screen.MovieDetailsScreen.route) {
            MovieDetailsScreen(navController = navController)
        }
    }
}

@Composable
fun MovieDetailsScreen(navController: NavHostController) {
    TODO("Not yet implemented")
}