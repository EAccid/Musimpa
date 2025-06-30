package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.eaccid.musimpa.data.local.room.MovieEntity
import com.eaccid.musimpa.utils.toMovie
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.map

class MovieListScreenViewModel(createPager: () -> Pager<Int, MovieEntity>) : ViewModel() {
    private val pager by lazy { createPager() }

    val pagerRoomFlow = pager.flow
        .map { it.map { entity -> entity.toMovie() } }
        .cachedIn(viewModelScope)

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i("MoviesViewModel temptest ----------------- ", "$this is cleared 2")
    }
}