package com.eaccid.musimpa.ui.moviedetails

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.eaccid.musimpa.ui.customview.ProgressView
import com.eaccid.musimpa.ui.theme.MusimpaTheme
import com.eaccid.musimpa.ui.uientities.MovieItem
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieDetailsScreen(movieId: String) {
    val context = LocalContext.current
    val viewModel: MovieDetailsScreenViewModel = koinViewModel()
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    MoviesDetailsScreenContent(viewState)
    Log.i("MusimpaApp", "MovieDetailsScreen: movie ${movieId} screen open")
}

@Composable
fun MoviesDetailsScreenContent(
    viewState: MovieDetailsScreenViewState
) {
    if (viewState is MovieDetailsScreenViewState.Success) {
        val dataItem: MovieItem = viewState.movie
        Column(
            modifier = Modifier
                .padding(16.dp)
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
                    val view = ProgressView(it).apply {

                    }
                    view.progress = dataItem.voteAverage?.times(10)?.toInt() ?: 0
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
    } else {
        Text(text = "todo no data error handling")
    }
}

@Composable
fun YoutubeScreen(
    videoKey: String,
    modifier: Modifier
) {
    Log.i("MusimpaApp", "MovieDetailsScreen: videoKey ${videoKey} youtube load")
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