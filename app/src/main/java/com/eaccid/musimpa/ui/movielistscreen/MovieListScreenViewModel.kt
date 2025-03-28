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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MovieListScreenViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    private val _pagingMoviesFlow = MutableStateFlow<PagingData<MovieItem>>(PagingData.empty())
    val pagingMoviesFlow: StateFlow<PagingData<MovieItem>> = _pagingMoviesFlow

    private val pager = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { DiscoverPagingSource(moviesRepository) }
    ).flow.cachedIn(viewModelScope)

    init {
        collectPagingData()
    }

    private fun collectPagingData() {
        Log.i("MoviesViewModel temptest ----------------- ", "$this collectPagingData")
        viewModelScope.launch {
            pager.collectLatest { pagingData ->
                _pagingMoviesFlow.value = pagingData
            }
        }
    }

    fun refreshMovies() {
        collectPagingData()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i("MoviesViewModel temptest ----------------- ", "$this is cleared 2")
    }
}