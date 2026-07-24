package com.example.mymovies.domain.usecase

import com.example.mymovies.core.common.result.NetworkResult
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiscoverMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(genreId: Int, page: Int): Flow<NetworkResult<List<Movie>>> {
        return repository.discoverMovies(genreId, page)
    }
}
