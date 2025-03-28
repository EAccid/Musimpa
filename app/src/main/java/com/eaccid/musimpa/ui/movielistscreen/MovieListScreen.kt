package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
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
import com.eaccid.musimpa.ui.SaveLastScreenEffect
import com.eaccid.musimpa.ui.Screen
import com.eaccid.musimpa.ui.uientities.MovieItem
import com.eaccid.musimpa.utils.PosterSize
import com.eaccid.musimpa.utils.toImageUri
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
    val currentPagingMoviesFlow: LazyPagingItems<MovieItem> =
        viewModel.getDiscoverMovies().collectAsLazyPagingItems()
    MovieListScreenContent(currentPagingMoviesFlow, onItemClicked = { movieItem ->
        navController.navigate(Screen.MovieDetailsScreen.route + "/${movieItem.id}")
    })
    BackHandler {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        } else {
            navController.navigate(Screen.MainScreen.route) {
                popUpTo(0)
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    SaveLastScreenEffect(Screen.MovieListScreen.route)

    DisposableEffect(Unit) {
        println("temptest DisposableEffect MovieListScreen Entered")
        onDispose { println("temptest DisposableEffect MovieListScreen Disposed") }
    }

}

@Composable
fun MovieListScreenContent(
    lazyPagingItems: LazyPagingItems<MovieItem>,
    onItemClicked: (movieItem: MovieItem) -> Unit
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize(1.0f)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
            item {
                Text(
                    text = "Waiting for items to load from the backend",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
        items(count = lazyPagingItems.itemCount) { index ->
            val item = lazyPagingItems[index]
            MovieItemView(dataItem = item!!, onItemClicked)
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
}

@Composable
fun MovieItemView(dataItem: MovieItem, onItemClick: (movieItem: MovieItem) -> Unit) {
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
                    text = dataItem.voteAverage.toString() ?: "Non"
                )
            }
        }
    }
}

//todo seems like we need MockMoviePagingSource to have a nice preview with LazyPagingItems<MovieItem>

//class MovieListScreenViewPreviewParameterProvider :
//    PreviewParameterProvider<MovieListScreenViewState> {
//    override val values = sequenceOf(
//        MovieListScreenViewState.Success, MovieListScreenViewState.Error(Throwable("error"))
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun MovieListScreenContentPreview(@PreviewParameter(MovieListScreenViewPreviewParameterProvider::class) viewState: MovieListScreenViewState) {
//    MusimpaTheme {
//        MovieListScreenContent(mutableListOf(
//            MovieItem(id = 1, title = "title 1"),
//            MovieItem(id = 2, title = "title 2"),
//            MovieItem(id = 3, title = "title 3")
//        ), onItemClicked = {})
//    }
//}