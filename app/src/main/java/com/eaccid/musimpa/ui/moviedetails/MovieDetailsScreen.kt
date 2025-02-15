package com.eaccid.musimpa.ui.moviedetails

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.eaccid.musimpa.javaclasses.UserScoreCustomView
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.ui.Screen
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import com.eaccid.musimpa.ui.uientities.MovieItem
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.compose.rememberKoinInject


@Composable
fun MovieDetailsScreen(
    movieId: String,
    navController: NavController,
    repository: MoviesRepository = rememberKoinInject<MoviesRepository>() //with Hilt its better to inject right into the ViewModel's constructor
) {

    // 1. This may not always follow Jetpack Navigation’s lifecycle handling,
    // leading to unexpected behavior in recompositions due to state loss
    // val viewModel: MovieDetailsScreenViewModel = koinViewModel<MovieDetailsScreenViewModel>()

    // 3. Hilt would be the best

    /** 4. Better way to get sate for now
     * for more details with rememberedGetBackStackEntry
     * see [com.eaccid.musimpa.ui.mainscreen.MainScreen] **/

    @SuppressLint("UnrememberedGetBackStackEntry")
    val navBackStackEntry: NavBackStackEntry = remember {
        navController.getBackStackEntry(Screen.MovieDetailsScreen.route + "/{movieId}")
    }
    navBackStackEntry.savedStateHandle["movieId"] = movieId //set manually if not using DI
    val factory = remember {
        MovieDetailsViewModelFactory(
            repository,
            navBackStackEntry.savedStateHandle
        )
    }
    val viewModel: MovieDetailsScreenViewModel = viewModel(
        viewModelStoreOwner = navBackStackEntry,
        factory = factory
    )
    SideEffect {
        Log.i(
            "twicetest @Composable//MovieDetailsScreen",
            "@Composable//MovieDetailsScreen ->> viewModel: $viewModel"
        )
    }
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    MoviesDetailsScreenContent(viewState)
    SideEffect {
        Log.i("MusimpaApp", "MovieDetailsScreen: movie ${movieId} screen open")
    }
}

@Composable
fun MoviesDetailsScreenContent(
    viewState: MovieDetailsScreenViewState
) {
    when (viewState) {
        is MovieDetailsScreenViewState.Success -> {
            val dataItem: MovieItem = viewState.movie
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .fillMaxWidth()

            ) {
                YoutubeScreen(
                    dataItem.videoKey,
                    Modifier
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 16.dp, 0.dp, 0.dp)
                ) {
                    AndroidView(modifier = Modifier
                        .width(100.dp)
                        .height(100.dp), factory = {
                        val view = UserScoreCustomView(it).apply {
                            /* todo refactor userscorecustomview to work with style properly
                            for now its just temp old java that does not work correctly
                            with object UserScoreCustomViewStyle.Attributes in Style.kt*/
                        }
                        view.score = dataItem.voteAverage?.times(10)?.toInt() ?: 0
                        view.setTextSize(24)
                        view
                    })
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 0.dp, 0.dp, 0.dp)
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
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 8.dp),
                            style = TextStyle(
                                fontStyle = FontStyle.Italic
                            ),
                            text = dataItem.tagline ?: "Non"
                        )
                        Text(
                            text = dataItem.releaseDate ?: "Non",
                        )
                        Text(
                            text = "${dataItem.runtime.toString()} min"
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .padding(0.dp, 16.dp, 0.dp, 0.dp),
                    text = "Overview",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                )
                Text(
                    text = dataItem.overview ?: "Non",
                    modifier = Modifier
                        .padding(0.dp, 4.dp, 0.dp, 0.dp)
                )
            }
        }

        else -> {
            Text(text = "todo 'no data' / 'error' handling")
        }
    }
}

@Composable
fun YoutubeScreen(
    videoKey: String,
    modifier: Modifier
) {
    SideEffect {
        Log.i("MusimpaApp", "MovieDetailsScreen: videoKey ${videoKey} youtube load")
    }
    val context = LocalContext.current
    AndroidView(factory = {
        val view = YouTubePlayerView(it)
        val fragment = view.addYouTubePlayerListener(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    youTubePlayer.cueVideo(videoKey, 0f)
                }
            }
        )
        view
    })
}

class MoviesDetailsScreenViewPreviewParameterProvider :
    PreviewParameterProvider<MovieDetailsScreenViewState> {
    override val values = sequenceOf(
        MovieDetailsScreenViewState.Success(MovieItem(id = 0)),
        MovieDetailsScreenViewState.NoData
    )
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenContentPreview(
    @PreviewParameter(MoviesDetailsScreenViewPreviewParameterProvider::class) viewState: MovieDetailsScreenViewState
) {
    MusimpaTheme {
        MoviesDetailsScreenContent(viewState)
    }
}