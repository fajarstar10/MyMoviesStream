package com.example.mymovies.domain.usecase

import com.example.mymovies.core.common.result.NetworkResult
import com.example.mymovies.domain.model.Genre
import com.example.mymovies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieGenresUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<NetworkResult<List<Genre>>> {
        return repository.getMovieGenres()
    }
}
