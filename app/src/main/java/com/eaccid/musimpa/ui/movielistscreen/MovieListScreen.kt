package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.ui.Screen
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import com.eaccid.musimpa.ui.uientities.MovieItem
import com.eaccid.musimpa.utils.PosterSize
import com.eaccid.musimpa.utils.toImageUri
import org.koin.compose.koinInject

@Composable
fun MovieListScreen(navController: NavController) {
    //would be great to have viewmodel injected by DI, but koin causes recomposition

    val moviesRepository = koinInject<MoviesRepository>()

    val navBackStackEntry = remember(navController) {
        navController.getBackStackEntry(Screen.MainScreen.route)
    }
    val factory by remember { lazy { MovieListScreenViewModelFactory(moviesRepository) } }
    val viewModel: MovieListScreenViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry,
        factory = factory
    )
    SideEffect {
        Log.i("twicetest ", " --------------- ")
        Log.i(
            "twicetest @Composable//MovieListScreen",
            "@Composable//MovieListScreen list ->> viewModel 2: $viewModel"
        )
    }
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    MovieListScreenContent(viewState, onItemClicked = { movieItem ->
        navController.navigate(Screen.MovieDetailsScreen.route + "/${movieItem.id}")
    })
}

@Composable
fun MovieListScreenContent(
    viewState: MovieListScreenViewState,
    onItemClicked: (movieItem: MovieItem) -> Unit,
) {
    if (viewState is MovieListScreenViewState.Success) {
        MoviesList(viewState.movies, onItemClicked)
    } else if (viewState is MovieListScreenViewState.Error) {
        Text(text = "todo error handling")
    }
}

@Composable
fun MoviesList(items: List<MovieItem>, onItemClicked: (movieItem: MovieItem) -> Unit) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState) {
        items(items.size, key = { index -> items[index].id }) { index ->
            MovieItemView(dataItem = items[index], onItemClicked)
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
        Row(modifier = Modifier.padding(16.dp)) {
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

class MovieListScreenViewPreviewParameterProvider :
    PreviewParameterProvider<MovieListScreenViewState> {
    override val values = sequenceOf(
        MovieListScreenViewState.Success(
            mutableListOf(
                MovieItem(id = 1, title = "title 1"),
                MovieItem(id = 2, title = "title 2"),
                MovieItem(id = 3, title = "title 3")
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun MovieListScreenContentPreview(@PreviewParameter(MovieListScreenViewPreviewParameterProvider::class) viewState: MovieListScreenViewState) {
    MusimpaTheme {
        MovieListScreenContent(viewState, {})
    }
}