package com.eaccid.musimpa.ui.movielistscreen.discover

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.eaccid.musimpa.domain.usecase.GetDiscoverMoviesUseCase
import com.eaccid.musimpa.ui.mappers.toMovieUi
import com.eaccid.musimpa.ui.models.MovieUi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MovieListScreenViewModel(
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase
) : ViewModel() {

    val pagerFlow: Flow<PagingData<MovieUi>> = flow {
        emitAll(
            getDiscoverMoviesUseCase()
                .map { pagingData ->
                    pagingData.map { movie -> movie.toMovieUi() }
                }
        )
    }.cachedIn(viewModelScope)

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i("MoviesViewModel temptest ----------------- ", "$this is cleared 2")
    }
}