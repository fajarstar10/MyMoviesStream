package com.example.mymovies.feature.home.presentation.discover

import androidx.lifecycle.viewModelScope
import com.example.mymovies.core.common.mvi.BaseViewModel
import com.example.mymovies.core.common.result.NetworkResult
import com.example.mymovies.domain.usecase.DiscoverMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val discoverMoviesUseCase: DiscoverMoviesUseCase
) : BaseViewModel<DiscoverContract.State, DiscoverContract.Intent, DiscoverContract.Effect>() {

    override fun createInitialState(): DiscoverContract.State = DiscoverContract.State()

    override fun handleIntent(intent: DiscoverContract.Intent) {
        when (intent) {
            is DiscoverContract.Intent.Initialize -> {
                if (uiState.value.movies.isEmpty()) {
                    setState { copy(genreId = intent.genreId, genreName = intent.genreName) }
                    loadMovies()
                }
            }
            is DiscoverContract.Intent.LoadMore -> loadMoreMovies()
            is DiscoverContract.Intent.OnMovieClicked -> {
                setEffect { DiscoverContract.Effect.NavigationToDetail(intent.movie.id) }
            }
            is DiscoverContract.Intent.OnBackClicked -> {
                setEffect { DiscoverContract.Effect.NavigateBack }
            }
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            discoverMoviesUseCase(uiState.value.genreId, 1).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> setState { copy(isLoading = true) }
                    is NetworkResult.Success -> setState {
                        copy(isLoading = false, movies = result.data, page = 1, canLoadMore = result.data.isNotEmpty(), errorMessage = null)
                    }
                    is NetworkResult.Error -> setState {
                        copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private fun loadMoreMovies() {
        if (uiState.value.isMoreLoading || !uiState.value.canLoadMore) return

        viewModelScope.launch {
            val nextPage = uiState.value.page + 1
            discoverMoviesUseCase(uiState.value.genreId, nextPage).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> setState { copy(isMoreLoading = true) }
                    is NetworkResult.Success -> {
                        setState {
                            copy(
                                isMoreLoading = false,
                                movies = movies + result.data,
                                page = nextPage,
                                canLoadMore = result.data.isNotEmpty()
                            )
                        }
                    }
                    is NetworkResult.Error -> setState { copy(isMoreLoading = false) }
                }
            }
        }
    }
}
