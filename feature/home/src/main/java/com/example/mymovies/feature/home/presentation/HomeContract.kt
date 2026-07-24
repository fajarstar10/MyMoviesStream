package com.example.mymovies.feature.home.presentation

import com.example.mymovies.core.common.mvi.MviEffect
import com.example.mymovies.core.common.mvi.MviIntent
import com.example.mymovies.core.common.mvi.MviState
import com.example.mymovies.domain.model.Genre
import com.example.mymovies.domain.model.Movie

class HomeContract {

    data class State(
        val isLoading: Boolean = false,
        val popularMovies: List<Movie> = emptyList(),
        val nowPlayingMovies: List<Movie> = emptyList(),
        val topRatedMovies: List<Movie> = emptyList(),
        val searchResults: List<Movie> = emptyList(),
        val genres: List<Genre> = emptyList(),
        val selectedGenre: Genre? = null,
        val searchQuery: String = "",
        val isSearching: Boolean = false,
        val popularPage: Int = 1,
        val canLoadMorePopular: Boolean = true,
        val isMoreLoading: Boolean = false,
        val errorMessage: String? = null
    ) : MviState

    sealed class Intent : MviIntent {
        object LoadHomeData : Intent()
        object LoadMorePopular : Intent()
        data class SearchMovie(val query: String) : Intent()
        data class OnGenreSelected(val genre: Genre?) : Intent()
        data class OnMovieClicked(val movie: Movie) : Intent()
        object OnSeeAllGenresClicked : Intent()
    }

    sealed class Effect : MviEffect {
        data class NavigationToDetail(val movieId: Int) : Effect()
        object NavigationToGenreList : Effect()
        data class NavigationToDiscover(val genreId: Int, val genreName: String) : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
