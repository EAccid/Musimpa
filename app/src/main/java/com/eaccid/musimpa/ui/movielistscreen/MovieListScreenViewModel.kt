package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eaccid.musimpa.repository.DiscoverPagingSource
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.ui.uientities.MovieItem
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow

class MovieListScreenViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {
    private var currentPagingMoviesFlow: Flow<PagingData<MovieItem>>? = null

    fun getDiscoverMovies(): Flow<PagingData<MovieItem>> {
        val newFlow = Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { DiscoverPagingSource(moviesRepository) }
        ).flow.cachedIn(viewModelScope)

        currentPagingMoviesFlow = newFlow
        return newFlow
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i("MoviesViewModel temptest ----------------- ", "$this is cleared 2")
    }
}