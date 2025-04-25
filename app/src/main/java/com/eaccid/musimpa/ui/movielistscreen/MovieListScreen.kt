package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eaccid.musimpa.data.domain.Movie
import com.eaccid.musimpa.ui.SaveLastScreenEffect
import com.eaccid.musimpa.ui.navigation.Screen
import com.eaccid.musimpa.utils.PosterSize
import com.eaccid.musimpa.utils.toImageUri
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieListScreen(navController: NavController) {
    val viewModel = koinViewModel<MovieListScreenViewModel>()
    SideEffect {
        Log.i("temptest ", " --------------- ")
        Log.i(
            "temptest @Composable//MovieListScreen",
            "@Composable//MovieListScreen list ->> viewModel 2: $viewModel"
        )
    }
    //try paging from room+api
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

    DisposableEffect(Unit) {
        println("temptest DisposableEffect MovieListScreen Entered")
        onDispose { println("temptest DisposableEffect MovieListScreen Disposed") }
    }

}

@Composable
fun MovieListScreenContent(
    lazyPagingItems: LazyPagingItems<Movie>,
    onItemClicked: (movie: Movie) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = lazyPagingItems.loadState) {
        if (lazyPagingItems.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error Occurred " + (lazyPagingItems.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
        if (lazyPagingItems.loadState.append is LoadState.Error) {
            Toast.makeText(
                context,
                "Error Occurred " + (lazyPagingItems.loadState.append as LoadState.Error).error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    PullToRefreshMovieLazyColumn(lazyPagingItems, onItemClicked)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshMovieLazyColumn(
    lazyPagingItems: LazyPagingItems<Movie>,
    onItemClicked: (movie: Movie) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            lazyPagingItems.refresh()
            isRefreshing = false
        }
    }

    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = pullToRefreshState,
        onRefresh = { isRefreshing = true },
        modifier = Modifier
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize(1.0f)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
                item {
                    Text(
                        text = "Waiting for items to load",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
            items(count = lazyPagingItems.itemCount) { index ->
                println("temptest item is null sometimes fix the problem ") //TODO
                lazyPagingItems[index]?.let { item ->
                    MovieItemView(dataItem = item, onItemClicked)
                }
            }
            if (lazyPagingItems.loadState.append == LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        val showScrollToTop by remember {
            derivedStateOf { lazyListState.firstVisibleItemIndex > 5 }
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
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Scroll to top")
            }
        }
    }
}

@Composable
fun MovieItemView(dataItem: Movie, onItemClick: (movie: Movie) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(dataItem) }
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(dataItem.posterPath?.toImageUri(PosterSize.W185) ?: "Non")
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentScale = ContentScale.FillWidth,
                placeholder = ColorPainter(Color.Gray),
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = dataItem.title ?: "Non",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                )
                Text(
                    text = dataItem.releaseDate ?: "Non",
                    modifier = Modifier
                        .padding(0.dp, 8.dp, 0.dp, 0.dp)
                )
                Text(
                    text = dataItem.voteAverage.toString()
                )
            }
        }
    }
}