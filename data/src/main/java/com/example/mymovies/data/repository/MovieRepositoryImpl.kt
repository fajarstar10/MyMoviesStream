package com.example.mymovies.data.repository

import com.example.mymovies.core.common.result.NetworkResult
import com.example.mymovies.core.network.api.TmdbApi
import com.example.mymovies.data.mapper.toDomain
import com.example.mymovies.domain.model.Genre
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.domain.model.Review
import com.example.mymovies.domain.model.Video
import com.example.mymovies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : MovieRepository {

    override fun getPopularMovies(page: Int, genreId: String?): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.getPopularMovies(page, genreId)
            emit(NetworkResult.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getNowPlayingMovies(page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.getNowPlayingMovies(page)
            emit(NetworkResult.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getTopRatedMovies(page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.getTopRatedMovies(page)
            emit(NetworkResult.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getMovieDetails(movieId: Int): Flow<NetworkResult<Movie>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.getMovieDetails(movieId)
            emit(NetworkResult.Success(response.toDomain()))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getMovieRecommendations(movieId: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.getMovieRecommendations(movieId)
            emit(NetworkResult.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun searchMovies(query: String, page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.searchMovies(query, page)
            emit(NetworkResult.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getMovieGenres(): Flow<NetworkResult<List<Genre>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.getMovieGenres()
            emit(NetworkResult.Success(response.genres.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getMovieReviews(movieId: Int, page: Int): Flow<NetworkResult<List<Review>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.getMovieReviews(movieId, page)
            emit(NetworkResult.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getMovieVideos(movieId: Int): Flow<NetworkResult<List<Video>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.getMovieVideos(movieId)
            emit(NetworkResult.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun discoverMovies(genreId: Int, page: Int): Flow<NetworkResult<List<Movie>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = api.discoverMovies(page, genreId)
            emit(NetworkResult.Success(response.results.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }
}
