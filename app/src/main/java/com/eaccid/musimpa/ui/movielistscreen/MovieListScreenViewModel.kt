package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.eaccid.musimpa.data.domain.Movie
import com.eaccid.musimpa.data.local.MovieEntity
import com.eaccid.musimpa.repository.DiscoverPagingSource
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.utils.toMovie
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MovieListScreenViewModel(
    private val moviesRepository: MoviesRepository,
    private val pagerRoom: Pager<Int, MovieEntity>
) : ViewModel() {

    private val _pagingMoviesFlow = MutableStateFlow<PagingData<Movie>>(PagingData.empty())
    val pagingMoviesFlow: StateFlow<PagingData<Movie>> = _pagingMoviesFlow

    private val pager = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { DiscoverPagingSource(moviesRepository) }
    ).flow.cachedIn(viewModelScope)

    val pagerRoomFlow = pagerRoom.flow.map { pagingData: PagingData<MovieEntity> ->
        pagingData.map { it.toMovie() }
    }.cachedIn(viewModelScope)


    init {
//        collectPagingData()
//        collectPagingDataWithRoom()
    }

    private fun collectPagingData() {
        Log.i("MoviesViewModel temptest ----------------- ", "$this collectPagingData")
        viewModelScope.launch {
            pager.collectLatest { pagingData ->
                _pagingMoviesFlow.value = pagingData
            }
        }
    }

    private fun collectPagingDataWithRoom() {
        Log.i("MoviesViewModel temptest ----------------- ", "$this collectPagingData")
        viewModelScope.launch {
            pagerRoomFlow.collectLatest { pagingData ->
                _pagingMoviesFlow.value = pagingData
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i("MoviesViewModel temptest ----------------- ", "$this is cleared 2")
    }
}