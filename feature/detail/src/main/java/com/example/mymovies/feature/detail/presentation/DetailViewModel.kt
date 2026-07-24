package com.example.mymovies.feature.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mymovies.core.common.mvi.BaseViewModel
import com.example.mymovies.core.common.result.NetworkResult
import com.example.mymovies.domain.usecase.GetMovieDetailsUseCase
import com.example.mymovies.domain.usecase.GetMovieRecommendationsUseCase
import com.example.mymovies.domain.usecase.GetMovieReviewsUseCase
import com.example.mymovies.domain.usecase.GetMovieVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieRecommendationsUseCase: GetMovieRecommendationsUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DetailContract.State, DetailContract.Intent, DetailContract.Effect>() {

    private val movieId: Int? = savedStateHandle.get<String>("movieId")?.toIntOrNull()

    override fun createInitialState(): DetailContract.State = DetailContract.State()

    init {
        movieId?.let {
            setIntent(DetailContract.Intent.LoadMovieDetails(it))
        }
    }

    override fun handleIntent(intent: DetailContract.Intent) {
        when (intent) {
            is DetailContract.Intent.LoadMovieDetails -> loadMovieDetails(intent.movieId)
            is DetailContract.Intent.LoadMoreReviews -> loadMoreReviews()
            is DetailContract.Intent.OnBackClicked -> setEffect { DetailContract.Effect.NavigateBack }
        }
    }

    private fun loadMovieDetails(id: Int) {
        viewModelScope.launch {
            combine(
                getMovieDetailsUseCase(id),
                getMovieRecommendationsUseCase(id),
                getMovieReviewsUseCase(id, 1),
                getMovieVideosUseCase(id)
            ) { details, recommendations, reviews, videos ->
                Quadruple(details, recommendations, reviews, videos)
            }.collect { (detailsResult, recommendationsResult, reviewsResult, videosResult) ->
                setState {
                    copy(
                        isLoading = detailsResult is NetworkResult.Loading,
                        movie = if (detailsResult is NetworkResult.Success) detailsResult.data else this.movie,
                        recommendations = if (recommendationsResult is NetworkResult.Success) recommendationsResult.data else this.recommendations,
                        reviews = if (reviewsResult is NetworkResult.Success) reviewsResult.data else this.reviews,
                        videos = if (videosResult is NetworkResult.Success) videosResult.data else this.videos,
                        errorMessage = (detailsResult as? NetworkResult.Error)?.message,
                        canLoadMoreReviews = if (reviewsResult is NetworkResult.Success) reviewsResult.data.isNotEmpty() else this.canLoadMoreReviews
                    )
                }
            }
        }
    }

    private fun loadMoreReviews() {
        val currentMovieId = movieId ?: return
        if (uiState.value.isReviewsLoading || !uiState.value.canLoadMoreReviews) return

        viewModelScope.launch {
            val nextPage = uiState.value.reviewPage + 1
            getMovieReviewsUseCase(currentMovieId, nextPage).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> setState { copy(isReviewsLoading = true) }
                    is NetworkResult.Success -> {
                        setState {
                            copy(
                                isReviewsLoading = false,
                                reviews = reviews + result.data,
                                reviewPage = nextPage,
                                canLoadMoreReviews = result.data.isNotEmpty()
                            )
                        }
                    }
                    is NetworkResult.Error -> setState { copy(isReviewsLoading = false) }
                }
            }
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
