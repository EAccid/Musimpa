package com.eaccid.musimpa.ui.movielistscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.eaccid.musimpa.data.repository.MoviesRepository
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.mappers.toMovie
import com.eaccid.musimpa.domain.models.Genre
import com.eaccid.musimpa.domain.models.MovieSearchFilter
import com.eaccid.musimpa.domain.models.SortOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
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

class MovieListScreenSearchAndFilterViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    // State flows for search and filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenres = MutableStateFlow<List<Int>>(emptyList())
    val selectedGenres: StateFlow<List<Int>> = _selectedGenres.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.POPULARITY_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _releaseYear = MutableStateFlow<Int?>(null)
    val releaseYear: StateFlow<Int?> = _releaseYear.asStateFlow()

    private val _ratingRange = MutableStateFlow<Pair<Double?, Double?>>(null to null)
    val ratingRange: StateFlow<Pair<Double?, Double?>> = _ratingRange.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Combine all filter states into a single filter object
    private val combinedFilter = combine(
        _searchQuery,
        _selectedGenres,
        _sortOption,
        _releaseYear,
        _ratingRange
    ) { searchQuery, genres, sort, year, rating ->
        MovieSearchFilter(
            searchQuery = searchQuery,
            selectedGenreIds = genres,
            sortBy = sort,
            releaseYear = year,
            minRating = rating.first,
            maxRating = rating.second
        )
    }

    // Debounced search query for API calls
    @OptIn(FlowPreview::class)
    private val debouncedSearchQuery = _searchQuery
        .debounce(300) // Wait 300ms after user stops typing
        .distinctUntilChanged()
        .filter { it.length >= 2 || it.isEmpty() } // Only search if 2+ chars or empty

    // Advanced Flow operations for movie pagination
    @OptIn(ExperimentalCoroutinesApi::class)
    val pagerFlow = combinedFilter
        .distinctUntilChanged()
        .onEach { filter ->
            Log.d("MovieListVM", "Filter changed: $filter")
            _isLoading.update { true }
            _errorMessage.update { null }
        }
        .flatMapLatest { filter ->
            when {
                filter.searchQuery.isNotBlank() -> {
                    Log.d("MovieListVM", "Using search pager")
                    repository.getSearchMoviesPager(filter.searchQuery, filter).flow
                }
                filter.selectedGenreIds.isNotEmpty() -> {
                    Log.d("MovieListVM", "Using genre pager with genres: ${filter.selectedGenreIds}")
                    // Fixed: Use discover with filter instead of genre-specific method
                    repository.getDiscoverMoviesWithFilter(filter).flow
                }
                else -> {
                    Log.d("MovieListVM", "Using discover pager")
                    repository.getDiscoverMoviesPager().flow
                }
            }
        }
        .map { pagingData ->
            pagingData.map { entity -> entity.toMovie() }
        }
        .onEach {
            Log.d("MovieListVM", "Paging data received")
            _isLoading.update { false }
        }
        .cachedIn(viewModelScope)

    // Advanced Flow for search suggestions (demonstrating more Flow operations)
    @OptIn(FlowPreview::class)
    val searchSuggestions = _searchQuery
        .debounce(150)
        .distinctUntilChanged()
        .filter { it.length >= 2 }
        .map { query ->
            // In a real app, you might have a suggestions API
            // For now, return empty list
            emptyList<String>()
        }

    init {
        loadGenres()
    }

    // Update functions
    fun updateSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    fun clearSearch() {
        _searchQuery.update { "" }
    }

    fun toggleGenre(genreId: Int) { //onGenreSelected
        _selectedGenres.update { current ->
            if (current.contains(genreId)) {
                current - genreId
            } else {
                current + genreId
            }
        }
    }

    fun clearAllGenres() {
        _selectedGenres.update { emptyList() }
    }

    fun updateSortOption(sortOption: SortOption) {
        _sortOption.update { sortOption }
    }

    fun updateReleaseYear(year: Int?) {
        _releaseYear.update { year }
    }

    fun updateRatingRange(min: Double?, max: Double?) {
        _ratingRange.update { min to max }
    }

    fun clearAllFilters() {
        _searchQuery.update { "" }
        _selectedGenres.update { emptyList() }
        _sortOption.update { SortOption.POPULARITY_DESC }
        _releaseYear.update { null }
        _ratingRange.update { null to null }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _isLoading.update { true }
            when (val result = repository.getGenres()) {
                is DataResult.Success -> {
                    _genres.update { result.data }
                }

                is DataResult.Failure -> {
                    _errorMessage.update { result.message ?: "Failed to load genres" }
                }

                is DataResult.NetworkError -> {
                    _errorMessage.update { "Network error loading genres" }
                }
            }
            _isLoading.update { false }
        }
    }

    fun clearError() {
        _errorMessage.update { null }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Log.i(
            "MovieListScreenSearchAndFilterViewModel temptest ----------------- ",
            "$this is cleared 2"
        )
    }

}