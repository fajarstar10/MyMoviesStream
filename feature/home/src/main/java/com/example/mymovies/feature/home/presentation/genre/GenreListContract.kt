package com.example.mymovies.feature.home.presentation.genre

import com.example.mymovies.core.common.mvi.MviEffect
import com.example.mymovies.core.common.mvi.MviIntent
import com.example.mymovies.core.common.mvi.MviState
import com.example.mymovies.domain.model.Genre

class GenreListContract {
    data class State(
        val isLoading: Boolean = false,
        val genres: List<Genre> = emptyList(),
        val errorMessage: String? = null
    ) : MviState

    sealed class Intent : MviIntent {
        object LoadGenres : Intent()
        data class OnGenreClicked(val genre: Genre) : Intent()
        object OnBackClicked : Intent()
    }

    sealed class Effect : MviEffect {
        data class NavigationToDiscover(val genreId: Int, val genreName: String) : Effect()
        object NavigateBack : Effect()
    }
}
