package com.eaccid.musimpa.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eaccid.musimpa.ui.mainscreen.MainScreen
import com.eaccid.musimpa.ui.moviedetails.MovieDetailsScreen
import com.eaccid.musimpa.ui.movieslist.MoviesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    SideEffect {
        Log.i("twicetest AppNavigation", "navController: $navController, ")
    }
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.MoviesScreen.route) {
            MoviesScreen(navController = navController)
        }
        composable(route = Screen.MovieDetailsScreen.route + "/{movieId}") { navBackStackEntry ->
            //todo trying how navigation arguments work
            val movieId = navBackStackEntry.arguments?.getString("movieId")
            movieId?.let { id -> MovieDetailsScreen(id, navController) }
        }
    }
}