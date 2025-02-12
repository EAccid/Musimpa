package com.eaccid.musimpa.ui

//todo consider changing Screen to Route
sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object MoviesScreen : Screen("movies_screen")
    object MovieDetailsScreen : Screen("movie_details_screen")
}