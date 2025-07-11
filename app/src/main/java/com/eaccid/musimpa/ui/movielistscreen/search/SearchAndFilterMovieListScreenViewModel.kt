package com.eaccid.musimpa.ui.movielistscreen.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.models.MovieSearchFilter
import com.eaccid.musimpa.domain.usecase.GetDiscoverMoviesUseCase
import com.eaccid.musimpa.domain.usecase.GetDiscoverMoviesWithFilterUseCase
import com.eaccid.musimpa.domain.usecase.GetGenresUseCase
import com.eaccid.musimpa.domain.usecase.GetSearchMoviesUseCase
import com.eaccid.musimpa.ui.mappers.toGenreUi
import com.eaccid.musimpa.ui.mappers.toMovieUi
import com.eaccid.musimpa.ui.models.GenreUi
import com.eaccid.musimpa.ui.models.MovieUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchAndFilterMovieListScreenViewModel(
    private val getSearchMoviesUseCase: GetSearchMoviesUseCase,
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase,
    private val getDiscoverMoviesWithFilterUseCase: GetDiscoverMoviesWithFilterUseCase,
    private val getGenresUseCase: GetGenresUseCase
) : ViewModel() {
    // UI State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenres = MutableStateFlow<List<Int>>(emptyList())
    val selectedGenres: StateFlow<List<Int>> = _selectedGenres.asStateFlow()

    // Genre-specific state (separate from movie paging state)
    private val _genresState = MutableStateFlow<GenresState>(GenresState.Loading)
    val genresState: StateFlow<GenresState> = _genresState.asStateFlow()

    // User Interactions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun toggleGenre(genreId: Int) {
        _selectedGenres.update { current ->
            if (genreId in current) current - genreId else current + genreId
        }
    }

    fun clearAllGenres() {
        _selectedGenres.value = emptyList()
    }

    // --- Filter Combination ---
    private val combinedFilter = combine(
        _searchQuery,
        _selectedGenres
    ) { query, genres ->
        MovieSearchFilter(
            searchQuery = query,
            selectedGenreIds = genres
        )
    }

    @OptIn(FlowPreview::class)
    private val debouncedFilter = combinedFilter
        .debounce(300)
        .distinctUntilChanged()
        .filter { it.searchQuery.length >= 2 || it.searchQuery.isEmpty() }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagerFlow: Flow<PagingData<MovieUi>> = debouncedFilter
        .onEach { Log.d("SearchViewModel", "Filter changed: $it") }
        .flatMapLatest { filter ->
            when {
                filter.searchQuery.isNotBlank() -> {
                    Log.d("SearchViewModel", "Using search pager")
                    getSearchMoviesUseCase(filter.searchQuery, filter)
                }

                filter.selectedGenreIds.isNotEmpty() -> {
                    Log.d("SearchViewModel", "Using genre pager: ${filter.selectedGenreIds}")
                    getDiscoverMoviesWithFilterUseCase(filter)
                }

                else -> {
                    Log.d("SearchViewModel", "Using default discover pager")
                    getDiscoverMoviesUseCase()
                }
            }
        }
        .onEach { Log.d("SearchViewModel", "Paging data received") }
        .map { pagingData ->
            pagingData.map { movie -> movie.toMovieUi() }
        }
        .cachedIn(viewModelScope)

    init {
        loadGenres()
    }

    // --- Genre Loading ---
    private fun loadGenres() {
        viewModelScope.launch {
            try {
                _genresState.value = GenresState.Loading
                when (val result = getGenresUseCase()) {
                    is DataResult.Success -> {
                        _genresState.value = GenresState.Success(result.data.map { it.toGenreUi() })
                    }

                    is DataResult.Failure -> {
                        Log.e("SearchViewModel", "Failed to load genres: ${result.message}")
                        _genresState.value = GenresState.Error(Exception(result.message))
                    }

                    is DataResult.NetworkError -> {
                        Log.e("SearchViewModel", "Network error while loading genres")
                        _genresState.value = GenresState.Error(Exception("Network error"))
                    }
                }
            } catch (e: Exception) {
                _genresState.value = GenresState.Error(e)
            }
        }
    }

    // Retry functions
    fun retryGenres() {
        loadGenres()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i("SearchViewModel", "ViewModel cleared")
    }
}

// Simplified sealed class only for genres
sealed class GenresState {
    data object Loading : GenresState()
    data class Success(val genres: List<GenreUi>) : GenresState()
    data class Error(val exception: Throwable) : GenresState()
}