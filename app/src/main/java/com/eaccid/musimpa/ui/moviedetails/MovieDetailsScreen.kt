package com.eaccid.musimpa.ui.moviedetails

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieDetailsScreen(movieId: String) {
    val context = LocalContext.current
    val viewModel: MovieDetailsScreenViewModel = koinViewModel()
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()

    Log.i("MusimpaApp", "MovieDetailsScreen: movie ${movieId} screen open")
}