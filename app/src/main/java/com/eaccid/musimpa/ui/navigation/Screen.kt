package com.eaccid.musimpa.ui.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main_screen")
    data object MovieList : Screen("movies_screen")
    data object SearchAndFilterMovieList : Screen("search_and_filter_movies_screen")
    data object MovieDetails : Screen("movie_details_screen") {
        fun createRoute(movieId: Int): String = "movie_details_screen/$movieId"
    }
}