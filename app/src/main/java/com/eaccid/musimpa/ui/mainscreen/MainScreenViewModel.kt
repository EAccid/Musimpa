package com.eaccid.musimpa.ui.mainscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.eaccid.musimpa.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    fun login() {

    }

    private val _uiState = MutableStateFlow(MainScreenState.NoData)
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()


    override fun onCleared() {
        super.onCleared()
        Log.i("MainScreenViewModel", "is cleared")
    }
}

