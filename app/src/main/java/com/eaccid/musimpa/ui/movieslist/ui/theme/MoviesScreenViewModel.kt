package com.eaccid.musimpa.ui.movieslist.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel

class MoviesScreenViewModel : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        Log.i("MusimpaApp", "MoviesViewModel is cleared")
    }
}