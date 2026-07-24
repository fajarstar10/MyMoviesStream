package com.example.mymovies.feature.home.presentation.genre

import androidx.lifecycle.viewModelScope
import com.example.mymovies.core.common.mvi.BaseViewModel
import com.example.mymovies.core.common.result.NetworkResult
import com.example.mymovies.domain.usecase.GetMovieGenresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreListViewModel @Inject constructor(
    private val getMovieGenresUseCase: GetMovieGenresUseCase
) : BaseViewModel<GenreListContract.State, GenreListContract.Intent, GenreListContract.Effect>() {

    override fun createInitialState(): GenreListContract.State = GenreListContract.State()

    override fun handleIntent(intent: GenreListContract.Intent) {
        when (intent) {
            is GenreListContract.Intent.LoadGenres -> loadGenres()
            is GenreListContract.Intent.OnGenreClicked -> {
                setEffect { GenreListContract.Effect.NavigationToDiscover(intent.genre.id, intent.genre.name) }
            }
            is GenreListContract.Intent.OnBackClicked -> {
                setEffect { GenreListContract.Effect.NavigateBack }
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            getMovieGenresUseCase().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> setState { copy(isLoading = true) }
                    is NetworkResult.Success -> setState {
                        copy(isLoading = false, genres = result.data, errorMessage = null)
                    }
                    is NetworkResult.Error -> setState {
                        copy(isLoading = false, errorMessage = result.message)
                    }
                }
            }
        }
    }
}
