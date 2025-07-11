package com.eaccid.musimpa.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eaccid.musimpa.ui.mainscreen.MainScreen
import com.eaccid.musimpa.ui.moviedetailsscreen.MovieDetailsScreen
import com.eaccid.musimpa.ui.movielistscreen.discover.MovieListScreen
import com.eaccid.musimpa.ui.movielistscreen.search.SearchAndFilterMovieListScreen

@Composable
fun AppNavigation(lastScreen: String) {
    val navController = rememberNavController()

    SideEffect {
        Log.i("temptest AppNavigation", "lastScreen: $lastScreen ")
        Log.i("temptest AppNavigation", "navC ontroller: $navController, ")
    }

    //add graph in case of complex navigation
    NavHost(
        navController = navController,
        startDestination = lastScreen // Screen.MainScreen.route by default
    ) {
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.MovieList.route) {
            MovieListScreen(navController = navController)
        }
        composable(route = Screen.SearchAndFilterMovieList.route) {
            SearchAndFilterMovieListScreen(navController = navController)
        }
        composable(
            route = Screen.MovieDetails.route + "/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val movieId = navBackStackEntry.arguments?.getString("movieId")
            movieId?.let { MovieDetailsScreen(it, navController) }
        }
    }
}