package com.eaccid.musimpa.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eaccid.musimpa.ui.mainscreen.MainScreen
import com.eaccid.musimpa.ui.moviedetailsscreen.MovieDetailsScreen
import com.eaccid.musimpa.ui.movielistscreen.MovieListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    //todo get from datastore
    val lastScreen = Screen.MainScreen.route //Screen.MovieListScreen.route
    SideEffect {
        Log.i("twicetest AppNavigation", "lastScreen: ${lastScreen} ")
        Log.i("twicetest AppNavigation", "navC ontroller: $navController, ")
    }

    //todo try graphs with extraction of auth and movies screens to separate logic
    NavHost(navController = navController, startDestination = lastScreen) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.MovieListScreen.route) {
            MovieListScreen(navController = navController)
        }
        composable(route = Screen.MovieDetailsScreen.route + "/{movieId}") { navBackStackEntry ->
            val movieId = navBackStackEntry.arguments?.getString("movieId")
            movieId?.let { MovieDetailsScreen(it, navController) }
        }
    }
}