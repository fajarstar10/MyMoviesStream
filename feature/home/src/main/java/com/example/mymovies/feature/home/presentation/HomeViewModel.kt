package com.example.mymovies.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.example.mymovies.core.common.mvi.BaseViewModel
import com.example.mymovies.core.common.result.NetworkResult
import com.example.mymovies.domain.usecase.GetMovieGenresUseCase
import com.example.mymovies.domain.usecase.GetNowPlayingMoviesUseCase
import com.example.mymovies.domain.usecase.GetPopularMoviesUseCase
import com.example.mymovies.domain.usecase.GetTopRatedMoviesUseCase
import com.example.mymovies.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase
) : BaseViewModel<HomeContract.State, HomeContract.Intent, HomeContract.Effect>() {

    private var searchJob: Job? = null

    override fun createInitialState(): HomeContract.State = HomeContract.State()

    override fun handleIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.LoadHomeData -> loadHomeData()
            is HomeContract.Intent.LoadMorePopular -> loadMorePopular()
            is HomeContract.Intent.SearchMovie -> {
                setState { copy(searchQuery = intent.query) }
                searchMovies(intent.query)
            }
            is HomeContract.Intent.OnGenreSelected -> {
                if (intent.genre == null) {
                    setState { copy(selectedGenre = null, popularPage = 1, canLoadMorePopular = true) }
                    loadHomeData()
                } else {
                    setEffect { HomeContract.Effect.NavigationToDiscover(intent.genre.id, intent.genre.name) }
                }
            }
            is HomeContract.Intent.OnMovieClicked -> {
                setEffect { HomeContract.Effect.NavigationToDetail(intent.movie.id) }
            }
            is HomeContract.Intent.OnSeeAllGenresClicked -> {
                setEffect { HomeContract.Effect.NavigationToGenreList }
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                getPopularMoviesUseCase(page = 1),
                getNowPlayingMoviesUseCase(),
                getTopRatedMoviesUseCase(),
                getMovieGenresUseCase()
            ) { popular, nowPlaying, topRated, genres ->
                DataPackage(popular, nowPlaying, topRated, genres)
            }.collect { results ->
                setState {
                    copy(
                        isLoading = results.popular is NetworkResult.Loading || 
                                    results.nowPlaying is NetworkResult.Loading || 
                                    results.topRated is NetworkResult.Loading ||
                                    results.genres is NetworkResult.Loading,
                        popularMovies = if (results.popular is NetworkResult.Success) results.popular.data else popularMovies,
                        nowPlayingMovies = if (results.nowPlaying is NetworkResult.Success) results.nowPlaying.data else nowPlayingMovies,
                        topRatedMovies = if (results.topRated is NetworkResult.Success) results.topRated.data else topRatedMovies,
                        genres = if (results.genres is NetworkResult.Success) results.genres.data else genres,
                        popularPage = 1,
                        canLoadMorePopular = true,
                        errorMessage = (results.popular as? NetworkResult.Error)?.message 
                            ?: (results.nowPlaying as? NetworkResult.Error)?.message
                            ?: (results.topRated as? NetworkResult.Error)?.message
                            ?: (results.genres as? NetworkResult.Error)?.message
                    )
                }
            }
        }
    }

    private fun loadMorePopular() {
        if (uiState.value.isMoreLoading || !uiState.value.canLoadMorePopular || uiState.value.searchQuery.isNotEmpty()) return

        viewModelScope.launch {
            val nextPage = uiState.value.popularPage + 1
            getPopularMoviesUseCase(page = nextPage).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> setState { copy(isMoreLoading = true) }
                    is NetworkResult.Success -> {
                        setState {
                            copy(
                                isMoreLoading = false,
                                popularMovies = popularMovies + result.data,
                                popularPage = nextPage,
                                canLoadMorePopular = result.data.isNotEmpty()
                            )
                        }
                    }
                    is NetworkResult.Error -> setState { copy(isMoreLoading = false) }
                }
            }
        }
    }

    private fun searchMovies(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            setState { copy(isSearching = false, searchResults = emptyList()) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            searchMoviesUseCase(query).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> setState { copy(isSearching = true) }
                    is NetworkResult.Success -> setState {
                        copy(isSearching = false, searchResults = result.data, errorMessage = null)
                    }
                    is NetworkResult.Error -> setState {
                        copy(isSearching = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private data class DataPackage(
        val popular: NetworkResult<List<com.example.mymovies.domain.model.Movie>>,
        val nowPlaying: NetworkResult<List<com.example.mymovies.domain.model.Movie>>,
        val topRated: NetworkResult<List<com.example.mymovies.domain.model.Movie>>,
        val genres: NetworkResult<List<com.example.mymovies.domain.model.Genre>>
    )
}
