package com.eaccid.musimpa.ui.moviedetails

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MovieDetailsScreen(movieId: String) {
    val context = LocalContext.current
    Log.i("MusimpaApp", "MovieDetailsScreen: movie ${movieId} clicked")
}