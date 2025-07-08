package com.eaccid.musimpa.domain.repository

import com.eaccid.musimpa.data.remote.ApiResponse
import com.eaccid.musimpa.data.remote.dto.DiscoverDto
import com.eaccid.musimpa.data.remote.dto.GenreListDto
import com.eaccid.musimpa.data.remote.dto.MovieCreditsDto
import com.eaccid.musimpa.data.remote.dto.MovieDto
import com.eaccid.musimpa.data.remote.dto.VideosResultDto
import com.eaccid.musimpa.data.remote.safeApiRequest
import com.eaccid.musimpa.data.remote.services.MovieApiService
import com.eaccid.musimpa.data.remote.services.MovieDiscoverAllQueryMap
import com.eaccid.musimpa.data.repository.MoviesRemoteDataSource
import com.eaccid.musimpa.domain.common.DataResult
import com.eaccid.musimpa.domain.models.MovieSearchFilter

class MoviesRemoteDataSourceImpl(
    private val apiService: MovieApiService
) : MoviesRemoteDataSource {

    override suspend fun discoverAll(page: Int): ApiResponse<DiscoverDto> =
        discoverAll(MovieDiscoverAllQueryMap(page = page))

    override suspend fun discoverAll(query: MovieDiscoverAllQueryMap): ApiResponse<DiscoverDto> {
        val params = query.toQueryMap()
        return safeApiRequest { apiService.discoverAll(params) }
    }

    override suspend fun searchMovies(
        searchQuery: String,
        page: Int,
        filter: MovieSearchFilter
    ): ApiResponse<DiscoverDto> {
        val queryMap = MovieDiscoverAllQueryMap(
            page = page,
            searchQuery = searchQuery,
            genreIds = filter.selectedGenreIds,
            sortBy = filter.sortBy.value,
            primaryReleaseYear = filter.releaseYear,
            voteAverageGte = filter.minRating,
            voteAverageLte = filter.maxRating
        )

        return safeApiRequest {
            if (searchQuery.isBlank()) {
                apiService.discoverAll(queryMap.toQueryMap())
            } else {
                apiService.searchMovies(queryMap.toQueryMap())
            }
        }
    }

    override suspend fun getGenres(): ApiResponse<GenreListDto> =
        safeApiRequest { apiService.getGenres(emptyMap()) }

    override suspend fun getMovie(movieId: Int): ApiResponse<MovieDto> =
        safeApiRequest {
            apiService.getMovie(movieId, emptyMap())
        }

    override suspend fun getMovieVideos(movieId: Int): ApiResponse<VideosResultDto> =
        safeApiRequest {
            apiService.getMovieVideos(movieId, emptyMap())
        }

    override suspend fun getMovieCredits(movieId: Int): ApiResponse<MovieCreditsDto> =
        safeApiRequest {
            apiService.getMovieCredits(movieId, emptyMap())
        }

}