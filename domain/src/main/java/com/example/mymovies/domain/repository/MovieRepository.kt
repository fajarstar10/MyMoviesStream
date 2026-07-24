package com.example.mymovies.domain.repository

import com.example.mymovies.core.common.result.NetworkResult
import com.example.mymovies.domain.model.Genre
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.domain.model.Review
import com.example.mymovies.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovies(page: Int, genreId: String? = null): Flow<NetworkResult<List<Movie>>>
    fun getNowPlayingMovies(page: Int): Flow<NetworkResult<List<Movie>>>
    fun getTopRatedMovies(page: Int): Flow<NetworkResult<List<Movie>>>
    fun getMovieDetails(movieId: Int): Flow<NetworkResult<Movie>>
    fun getMovieRecommendations(movieId: Int): Flow<NetworkResult<List<Movie>>>
    fun searchMovies(query: String, page: Int): Flow<NetworkResult<List<Movie>>>
    fun getMovieGenres(): Flow<NetworkResult<List<Genre>>>
    fun getMovieReviews(movieId: Int, page: Int): Flow<NetworkResult<List<Review>>>
    fun getMovieVideos(movieId: Int): Flow<NetworkResult<List<Video>>>
    fun discoverMovies(genreId: Int, page: Int): Flow<NetworkResult<List<Movie>>>
}
