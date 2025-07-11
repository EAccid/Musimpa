package com.eaccid.musimpa.ui.movielistscreen.search


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.eaccid.musimpa.ui.component.LogCompositions
import com.eaccid.musimpa.ui.component.SaveLastScreenEffect
import com.eaccid.musimpa.ui.models.GenreUi
import com.eaccid.musimpa.ui.models.MovieUi
import com.eaccid.musimpa.ui.movielistscreen.ErrorStateContent
import com.eaccid.musimpa.ui.movielistscreen.PullToRefreshMovieLazyColumn
import com.eaccid.musimpa.ui.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchAndFilterMovieListScreen(navController: NavController) {
    LogCompositions("MovieListSearchAndFilterScreen")

    val viewModel = koinViewModel<SearchAndFilterMovieListScreenViewModel>()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGenres by viewModel.selectedGenres.collectAsState()
    val genresState by viewModel.genresState.collectAsState()
    val lazyPagingItems = viewModel.pagerFlow.collectAsLazyPagingItems()

    val onItemClicked = { movie: MovieUi ->
        navController.navigate(Screen.MovieDetails.createRoute(movie.id)) {
            restoreState = true
        }
    }

    // Handle back press
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

    Column(modifier = Modifier.fillMaxSize()) {
        SearchTextField(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateSearchQuery,
            onClearSearch = viewModel::clearSearch
        )

        // Genre filter section with state handling
        when (genresState) {
            is GenresState.Loading -> {
                GenreLoadingSection()
            }

            is GenresState.Success -> {
                GenreFilterSection(
                    genres = (genresState as GenresState.Success).genres,
                    selectedGenres = selectedGenres,
                    onToggleGenre = viewModel::toggleGenre,
                    onClearAllGenres = viewModel::clearAllGenres
                )
            }

            is GenresState.Error -> {
                GenreErrorSection(
                    message = (genresState as GenresState.Error).exception.message
                        ?: "Failed to load genres",
                    onRetry = viewModel::retryGenres
                )
            }
        }

        SearchMovieListScreenContent(
            lazyPagingItems = lazyPagingItems,
            searchQuery = searchQuery,
            selectedGenres = selectedGenres,
            genresState = genresState,
            onItemClicked = onItemClicked
        )
    }

    SaveLastScreenEffect(Screen.SearchAndFilterMovieList.route)
}


@Composable
private fun SearchMovieListScreenContent(
    lazyPagingItems: LazyPagingItems<MovieUi>,
    searchQuery: String,
    selectedGenres: List<Int>,
    genresState: GenresState,
    onItemClicked: (movie: MovieUi) -> Unit
) {
    // Movie list with state handling
    when (lazyPagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            // Show loading only if we don't have any items yet
            if (lazyPagingItems.itemCount == 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        is LoadState.Error -> {
            val error = lazyPagingItems.loadState.refresh as LoadState.Error
            ErrorStateContent(
                message = error.error.message ?: "Failed to load movies",
                onRetry = { lazyPagingItems.retry() }
            )
        }

        is LoadState.NotLoading -> {
            if (lazyPagingItems.itemCount == 0) {
                // Empty state
                EmptyMovieState(
                    searchQuery = searchQuery,
                    selectedGenres = selectedGenres,
                    genresState = genresState
                )
            } else {
                // Success state - show the list
                PullToRefreshMovieLazyColumn(
                    lazyPagingItems = lazyPagingItems,
                    onItemClicked = onItemClicked
                )
            }
        }
    }
}


@Composable
private fun SearchTextField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = {
            Text(
                "Search movies",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = {
            Text(
                "Enter movie title...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                ClearSearchButton(
                    onClick = {
                        onClearSearch()
                        Log.d("SearchField", "Clear button clicked")
                    }
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                // Handle search action if needed
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ClearSearchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "Clear search",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun GenreLoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Loading genres...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun GenreErrorSection(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRetry) {
                Text(
                    text = "Retry",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun GenreFilterSection(
    genres: List<GenreUi>,
    selectedGenres: List<Int>,
    onToggleGenre: (Int) -> Unit,
    onClearAllGenres: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(
                items = genres,
                key = { it.id }
            ) { genre ->
                GenreFilterChip(
                    genre = genre,
                    isSelected = selectedGenres.contains(genre.id),
                    onClick = { onToggleGenre(genre.id) },
                    modifier = Modifier
                        .animateItem()
                        .padding(end = 4.dp)
                )
            }
        }

        // Clear all genres button
        AnimatedVisibility(
            visible = selectedGenres.isNotEmpty(),
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut(),
            modifier = Modifier.align(Alignment.End)
        ) {
            ClearAllGenresButton(
                onClick = onClearAllGenres,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
private fun GenreFilterChip(
    genre: GenreUi,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

@Composable
private fun ClearAllGenresButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = modifier
    ) {
        Icon(
            Icons.Default.Clear,
            contentDescription = "Clear all genres",
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun EmptyMovieState(
    searchQuery: String,
    selectedGenres: List<Int>,
    genresState: GenresState
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "No results",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            val message = when {
                searchQuery.isNotBlank() -> "No movies found for '$searchQuery'"
                selectedGenres.isNotEmpty() && genresState is GenresState.Success -> {
                    val genreNames = genresState.genres
                        .filter { it.id in selectedGenres }
                        .joinToString(", ") { it.name }
                    "No movies found for genres: $genreNames"
                }

                else -> "No movies available"
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
