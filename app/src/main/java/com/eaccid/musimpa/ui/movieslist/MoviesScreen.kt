package com.eaccid.musimpa.ui.movieslist

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eaccid.musimpa.ui.Screen
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import com.eaccid.musimpa.ui.uientities.MovieItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun MoviesScreen(navController: NavController) {
    val context = LocalContext.current //todo check if there is better solution
    val viewModel: MoviesScreenViewModel = koinViewModel()
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    MoviesScreenContent(viewState, onItemClicked = { movieItem ->
        navController.navigate(Screen.MovieDetailsScreen.route + "/${movieItem.id}")
        Log.i("MusimpaApp", "MoviesScreen: movie ${movieItem.id} - ${movieItem.title} clicked")
    }
    )
}

@Composable
fun MoviesScreenContent(
    viewState: MoviesScreenViewState,
    onItemClicked: (movieItem: MovieItem) -> Unit,
) {
    if (viewState is MoviesScreenViewState.Success) {
        MoviesList(viewState.movies, onItemClicked)
    } else if (viewState is MoviesScreenViewState.Error) {
        Text(text = "todo error handling")
    }
}

@Composable
fun MoviesList(items: List<MovieItem>, onItemClicked: (movieItem: MovieItem) -> Unit) {
    val lazyListState = rememberLazyListState()
    LazyColumn(state = lazyListState) {
        items(items.size) { index ->
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
            .padding(8.dp)
    ) {
        Text(
            text = dataItem.title ?: "Non",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenPreview() {
    MusimpaTheme {
        MoviesScreen(rememberNavController())
    }
}
