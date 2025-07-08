package com.eaccid.musimpa.ui.movielistscreen


import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eaccid.musimpa.domain.models.Movie
import com.eaccid.musimpa.domain.models.SortOption
import com.eaccid.musimpa.ui.component.LogCompositions
import com.eaccid.musimpa.ui.component.SaveLastScreenEffect
import com.eaccid.musimpa.ui.navigation.Screen
import com.eaccid.musimpa.utils.PosterSize
import com.eaccid.musimpa.utils.toImageUri
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieListScreen(navController: NavController) {
    LogCompositions("MovieListScreen")

    val viewModel = koinViewModel<MovieListScreenSearchAndFilterViewModel>()
    val uiState by viewModel.collectUiState()

    val movies: LazyPagingItems<Movie> = viewModel.pagerFlow.collectAsLazyPagingItems()

    val onItemClicked = { movie: Movie ->
        navController.navigate(Screen.MovieDetails.createRoute(movie.id)) {
            restoreState = true
        }
    }

    MovieListScreenContent(
        uiState = uiState,
        movies = movies,
        onItemClicked = onItemClicked,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onClearSearch = viewModel::clearSearch,
        onGenreToggle = viewModel::toggleGenre,
        onSortOptionChange = viewModel::updateSortOption,
        onReleaseYearChange = viewModel::updateReleaseYear,
        onRatingRangeChange = viewModel::updateRatingRange,
        onClearAllFilters = viewModel::clearAllFilters,
        onClearError = viewModel::clearError
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
private fun MovieListScreenSearchAndFilterViewModel.collectUiState(): State<MovieListUiState> {
    val searchQuery by searchQuery.collectAsStateWithLifecycle()
    val selectedGenres by selectedGenres.collectAsStateWithLifecycle()
    val sortOption by sortOption.collectAsStateWithLifecycle()
    val releaseYear by releaseYear.collectAsStateWithLifecycle()
    val ratingRange by ratingRange.collectAsStateWithLifecycle()
    val genres by genres.collectAsStateWithLifecycle()
    val isLoading by isLoading.collectAsStateWithLifecycle()
    val errorMessage by errorMessage.collectAsStateWithLifecycle()

    return remember(
        searchQuery, selectedGenres, sortOption, releaseYear,
        ratingRange, genres, isLoading, errorMessage
    ) {
        derivedStateOf {
            MovieListUiState(
                searchQuery = searchQuery,
                selectedGenres = selectedGenres,
                sortOption = sortOption,
                releaseYear = releaseYear,
                ratingRange = ratingRange,
                genres = genres,
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
    }
}

data class MovieListUiState(
    val searchQuery: String = "",
    val selectedGenres: List<Int> = emptyList(),
    val sortOption: SortOption = SortOption.POPULARITY_DESC,
    val releaseYear: Int? = null,
    val ratingRange: Pair<Double?, Double?> = null to null,
    val genres: List<com.eaccid.musimpa.domain.models.Genre> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


@Composable
fun MovieListScreenContent(
    uiState: MovieListUiState,
    movies: LazyPagingItems<Movie>,
    onItemClicked: (Movie) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onGenreToggle: (Int) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onReleaseYearChange: (Int?) -> Unit,
    onRatingRangeChange: (Double?, Double?) -> Unit,
    onClearAllFilters: () -> Unit,
    onClearError: () -> Unit
) {
    val context = LocalContext.current
    var showFilters by rememberSaveable("filters_visibility") { mutableStateOf(false) }

    LaunchedEffect(movies.loadState) {
        when {
            movies.loadState.refresh is LoadState.Error -> {
                val error = (movies.loadState.refresh as LoadState.Error).error
                Toast.makeText(context, "Load error: ${error.message}", Toast.LENGTH_SHORT).show()
            }

            movies.loadState.append is LoadState.Error -> {
                val error = (movies.loadState.append as LoadState.Error).error
                Toast.makeText(context, "Load error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // UI state error
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onClearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Search and Filter Header
        SearchAndFilterHeader(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onClearSearch = onClearSearch,
            onToggleFilters = { showFilters = !showFilters },
            hasActiveFilters = uiState.selectedGenres.isNotEmpty() ||
                    uiState.releaseYear != null ||
                    uiState.ratingRange.first != null ||
                    uiState.ratingRange.second != null
        )

        // Filters Section
        if (showFilters) {
            FiltersSection(
                uiState = uiState,
                onGenreToggle = onGenreToggle,
                onSortOptionChange = onSortOptionChange,
                onReleaseYearChange = onReleaseYearChange,
                onRatingRangeChange = onRatingRangeChange,
                onClearAllFilters = onClearAllFilters
            )
        }

        // Movies List
        MoviesList(
            movies = movies,
            onItemClicked = onItemClicked,
            isLoading = uiState.isLoading
        )
    }
}

@Composable
private fun SearchAndFilterHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onToggleFilters: () -> Unit,
    hasActiveFilters: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search movies") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Toggle Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onToggleFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Filters")
                    if (hasActiveFilters) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge {
                            Text("●")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltersSection(
    uiState: MovieListUiState,
    onGenreToggle: (Int) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onReleaseYearChange: (Int?) -> Unit,
    onRatingRangeChange: (Double?, Double?) -> Unit,
    onClearAllFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onClearAllFilters) {
                    Text("Clear all")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Genres Filter
            if (uiState.genres.isNotEmpty()) {
                Text(
                    text = "Genres",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(uiState.genres.size) { index ->
                        val genre = uiState.genres[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGenreToggle(genre.id) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = uiState.selectedGenres.contains(genre.id),
                                onCheckedChange = { onGenreToggle(genre.id) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = genre.name)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesList(
    movies: LazyPagingItems<Movie>,
    onItemClicked: (Movie) -> Unit,
    isLoading: Boolean
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            movies.refresh()
            isRefreshing = false
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = pullToRefreshState,
        onRefresh = { isRefreshing = true },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (movies.loadState.refresh == LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(count = movies.itemCount) { index ->
                movies[index]?.let { movie ->
                    MovieItemCard(
                        movie = movie,
                        onItemClicked = onItemClicked
                    )
                }
            }

            if (movies.loadState.append == LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        // Scroll to top FAB
        val showScrollToTop by remember {
            derivedStateOf { lazyListState.firstVisibleItemIndex > 3 }
        }

        if (showScrollToTop) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}

@Composable
private fun MovieItemCard(
    movie: Movie,
    onItemClicked: (Movie) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClicked(movie) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Movie Poster
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterPath?.toImageUri(PosterSize.W185) ?: "")
                    .crossfade(true)
                    .build(),
                contentDescription = "Movie poster ${movie.title}",
                modifier = Modifier
                    .size(120.dp, 180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Movie Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = movie.title ?: "Untitled",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                movie.releaseDate?.let { date ->
                    Text(
                        text = "Release date: $date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", movie.voteAverage),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                movie.overview?.let { overview ->
                    Text(
                        text = overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}