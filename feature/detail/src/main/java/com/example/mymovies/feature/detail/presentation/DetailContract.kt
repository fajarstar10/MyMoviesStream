package com.example.mymovies.feature.detail.presentation

import com.example.mymovies.core.common.mvi.MviEffect
import com.example.mymovies.core.common.mvi.MviIntent
import com.example.mymovies.core.common.mvi.MviState
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.domain.model.Review
import com.example.mymovies.domain.model.Video

class DetailContract {

    data class State(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
        val recommendations: List<Movie> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val videos: List<Video> = emptyList(),
        val reviewPage: Int = 1,
        val isReviewsLoading: Boolean = false,
        val canLoadMoreReviews: Boolean = true,
        val errorMessage: String? = null
    ) : MviState

    sealed class Intent : MviIntent {
        data class LoadMovieDetails(val movieId: Int) : Intent()
        object LoadMoreReviews : Intent()
        object OnBackClicked : Intent()
    }

    sealed class Effect : MviEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
