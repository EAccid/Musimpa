package com.eaccid.musimpa.ui.movielistscreen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.eaccid.musimpa.domain.models.Movie
import com.eaccid.musimpa.ui.component.LogCompositions
import com.eaccid.musimpa.ui.component.SaveLastScreenEffect
import com.eaccid.musimpa.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieListScreen(navController: NavController) {
    LogCompositions("MovieListScreen")

    val viewModel = koinViewModel<MovieListScreenViewModel>()

    //trying paging from room+api
    val currentPagingMoviesFlow: LazyPagingItems<Movie> =
        viewModel.pagerRoomFlow.collectAsLazyPagingItems()
    val onItemClicked = { movie: Movie ->
        navController.navigate(Screen.MovieDetails.createRoute(movie.id)) {
            restoreState = true
        }
    }
    MovieListScreenContent(currentPagingMoviesFlow, onItemClicked)
    BackHandler {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        } else {
            navController.navigate(Screen.Main.route) {
                popUpTo(0)
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    SaveLastScreenEffect(Screen.MovieList.route)
}

@Composable
fun MovieListScreenContent(
    lazyPagingItems: LazyPagingItems<Movie>,
    onItemClicked: (movie: Movie) -> Unit
) {
    lazyPagingItems.HandleLoadStates()
    PullToRefreshMovieLazyColumn(lazyPagingItems, onItemClicked)
}

