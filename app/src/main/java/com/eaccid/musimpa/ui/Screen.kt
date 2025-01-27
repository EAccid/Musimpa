package com.eaccid.musimpa.ui

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object MoviesScreen : Screen("movies_screen")
    object MovieDetailsScreen : Screen("movie_details_screen")
}