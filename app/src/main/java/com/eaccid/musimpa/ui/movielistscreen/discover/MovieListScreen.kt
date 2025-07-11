package com.eaccid.musimpa.ui.movielistscreen.discover

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.eaccid.musimpa.ui.component.LogCompositions
import com.eaccid.musimpa.ui.component.SaveLastScreenEffect
import com.eaccid.musimpa.ui.models.MovieUi
import com.eaccid.musimpa.ui.movielistscreen.EmptyStateContent
import com.eaccid.musimpa.ui.movielistscreen.ErrorStateContent
import com.eaccid.musimpa.ui.movielistscreen.PullToRefreshMovieLazyColumn
import com.eaccid.musimpa.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieListScreen(navController: NavController) {
    LogCompositions("MovieListScreen")

    val viewModel = koinViewModel<MovieListScreenViewModel>()

    val currentPagingMoviesFlow: LazyPagingItems<MovieUi> =
        viewModel.pagerFlow.collectAsLazyPagingItems()

    val onItemClicked = { movie: MovieUi ->
        navController.navigate(Screen.MovieDetails.createRoute(movie.id)) {
            restoreState = true
        }
    }

    MovieListScreenContent(
        currentPagingMoviesFlow,
        onItemClicked
    )

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
    lazyPagingItems: LazyPagingItems<MovieUi>, onItemClicked: (movie: MovieUi) -> Unit
) {
    // Handle different refresh states
    when (lazyPagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            // Show full screen loading only on initial load
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            // Show error state with retry
            val error = lazyPagingItems.loadState.refresh as LoadState.Error
            ErrorStateContent(message = error.error.message ?: "Failed to load movies",
                onRetry = { lazyPagingItems.retry() })
        }

        is LoadState.NotLoading -> {
            if (lazyPagingItems.itemCount == 0) {
                // Empty state
                EmptyStateContent()
            } else {
                // Success state - show the list with pull to refresh
                PullToRefreshMovieLazyColumn(lazyPagingItems, onItemClicked)
            }
        }
    }
}