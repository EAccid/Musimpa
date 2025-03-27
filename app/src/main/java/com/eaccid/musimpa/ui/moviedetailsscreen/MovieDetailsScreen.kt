package com.eaccid.musimpa.ui.moviedetailsscreen

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavController
import com.eaccid.musimpa.javaclasses.UserScoreCustomView
import com.eaccid.musimpa.ui.SaveLastScreenEffect
import com.eaccid.musimpa.ui.Screen
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import com.eaccid.musimpa.ui.uientities.MovieItem
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.androidx.compose.koinViewModel


@Composable
fun MovieDetailsScreen(
    movieId: String,
    navController: NavController,
) {
    val viewModel = koinViewModel<MovieDetailsScreenViewModel>()

    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    MoviesDetailsScreenContent(viewState)
    SideEffect {
        Log.i("MusimpaApp", "MovieDetailsScreen: movie ${movieId} screen open")
        Log.i(
            "temptest @Composable//MovieDetailsScreen",
            "@Composable//MovieDetailsScreen ->> viewModel 3: $viewModel"
        )
    }
//    BackHandler {
//        navController.popBackStack()
//    }

    SaveLastScreenEffect(Screen.MovieDetailsScreen.route + "/${movieId}")

    DisposableEffect(Unit) {
        println("temptest DisposableEffect MovieDetailsScreen Entered")
        onDispose { println("temptest DisposableEffect MovieDetailsScreen Disposed") }
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
    AndroidView(factory = {
        val youTubePlayerView = YouTubePlayerView(it)
        youTubePlayerView.addYouTubePlayerListener(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    youTubePlayer.cueVideo(videoKey, 0f)
                }
            }
        )
        youTubePlayerView
    }, modifier = modifier, onRelease = { youTubePlayerView ->
        // Dispose the player when the Composable leaves Composition to prevent a memory leak
        youTubePlayerView.release()
    })

    // no need anymore as onReleased was introduced
//    DisposableEffect(Unit) {
//        onDispose {
//            Log.i("MusimpaApp", "Releasing YouTubePlayerView")
//            youTubePlayerView.release()
//        }
//    }

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
fun MovieDetailsScreenContentPreview(
    @PreviewParameter(MoviesDetailsScreenViewPreviewParameterProvider::class) viewState: MovieDetailsScreenViewState
) {
    MusimpaTheme {
        MoviesDetailsScreenContent(viewState)
    }
}