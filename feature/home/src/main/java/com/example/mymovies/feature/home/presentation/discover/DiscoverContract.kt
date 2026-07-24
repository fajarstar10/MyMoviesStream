package com.example.mymovies.feature.home.presentation.discover

import com.example.mymovies.core.common.mvi.MviEffect
import com.example.mymovies.core.common.mvi.MviIntent
import com.example.mymovies.core.common.mvi.MviState
import com.example.mymovies.domain.model.Movie

class DiscoverContract {
    data class State(
        val genreId: Int = 0,
        val genreName: String = "",
        val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val page: Int = 1,
        val canLoadMore: Boolean = true,
        val isMoreLoading: Boolean = false,
        val errorMessage: String? = null
    ) : MviState

    sealed class Intent : MviIntent {
        data class Initialize(val genreId: Int, val genreName: String) : Intent()
        object LoadMore : Intent()
        data class OnMovieClicked(val movie: Movie) : Intent()
        object OnBackClicked : Intent()
    }

    sealed class Effect : MviEffect {
        data class NavigationToDetail(val movieId: Int) : Effect()
        object NavigateBack : Effect()
    }
}
