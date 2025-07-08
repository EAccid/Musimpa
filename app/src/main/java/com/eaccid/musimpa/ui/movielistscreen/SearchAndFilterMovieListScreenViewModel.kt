package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.eaccid.musimpa.data.repository.MoviesRepository
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.mappers.toMovie
import com.eaccid.musimpa.domain.models.Genre
import com.eaccid.musimpa.domain.models.Movie
import com.eaccid.musimpa.domain.models.MovieSearchFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    private val repository: MoviesRepository
) : ViewModel() {

    // --- UI State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenres = MutableStateFlow<List<Int>>(emptyList())
    val selectedGenres: StateFlow<List<Int>> = _selectedGenres.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    // --- User Interactions ---
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


    //TODO Add error handling for the search and genre loading states
    // + consider adding SavedStateHandle to persist state like search query and selected genre
    // Movie to UI model

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
    val pagerFlow: Flow<PagingData<Movie>> = debouncedFilter
        .onEach { Log.d("MoviesViewModel", "Filter changed: $it") }
        .flatMapLatest { filter ->
            when {
                filter.searchQuery.isNotBlank() -> {
                    Log.d("MoviesViewModel", "Using search pager")
                    repository.getSearchMoviesPager(filter.searchQuery, filter).flow
                }

                filter.selectedGenreIds.isNotEmpty() -> {
                    Log.d("MoviesViewModel", "Using genre pager: ${filter.selectedGenreIds}")
                    repository.getDiscoverMoviesWithFilter(filter).flow
                }

                else -> {
                    Log.d("MoviesViewModel", "Using default discover pager")
                    repository.getDiscoverMoviesPager().flow
                }
            }
        }
        .map { pagingData -> pagingData.map { it.toMovie() } } //TODO to UI Model
        .onEach { Log.d("MoviesViewModel", "Paging data received") }
        .catch { e -> Log.e("MoviesViewModel", "Pager flow error", e) }
        .cachedIn(viewModelScope)

    init {
        loadGenres()
    }

    // --- Genre Loading ---
    private fun loadGenres() {
        viewModelScope.launch {
            when (val result = repository.getGenres()) {
                is DataResult.Success -> _genres.value = result.data
                is DataResult.Failure -> Log.e(
                    "MoviesViewModel",
                    "Failed to load genres: ${result.message}"
                )

                is DataResult.NetworkError -> Log.e(
                    "MoviesViewModel",
                    "Network error while loading genres"
                )
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i("MoviesViewModel", "ViewModel cleared")
    }
}