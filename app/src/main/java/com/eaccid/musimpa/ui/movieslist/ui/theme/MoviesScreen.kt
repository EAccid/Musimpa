package com.eaccid.musimpa.ui.movieslist.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.eaccid.musimpa.utils.showToast
import org.koin.androidx.compose.koinViewModel


@Composable
fun MoviesScreen(navController: NavController) {
    val context = LocalContext.current //todo check if there is better solution
    val viewModel: MoviesScreenViewModel = koinViewModel()
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    MoviesScreenContent(viewState, onItemClicked = {
        //todo pass itemId somehow
        navController.navigate(Screen.MovieDetailsScreen.route)
        context.showToast("onItemClicked")
    })
}


@Composable
fun MoviesScreenContent(
    viewState: MoviesScreenViewState,
    onItemClicked: (movieItem: MovieItem) -> Unit,
) {
    if (viewState is MoviesScreenViewState.Success) {
        LazyListWithData(viewState.movies)
    } else if (viewState is MoviesScreenViewState.Error) {
        Text(text = "todo error")
    }
}

@Composable
fun LazyListWithData(items: List<MovieItem>) {
    LazyColumn {
        items(items.size) { index ->
            MovieItemView(dataItem = items[index])
        }
    }
}

@Composable
fun MovieItemView(dataItem: MovieItem) {
    Text(
        text = dataItem.title ?: "Non",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenPreview() {
    MusimpaTheme {
        MoviesScreen(rememberNavController())
    }
}
