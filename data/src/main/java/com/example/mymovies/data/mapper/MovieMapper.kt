package com.example.mymovies.data.mapper

import com.example.mymovies.core.network.api.TmdbApi
import com.example.mymovies.core.network.model.MovieResponse
import com.example.mymovies.core.network.model.ReviewResponse
import com.example.mymovies.core.network.model.VideoResponse
import com.example.mymovies.domain.model.Movie
import com.example.mymovies.domain.model.Review
import com.example.mymovies.domain.model.Video

fun MovieResponse.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "${TmdbApi.IMAGE_BASE_URL}$it" },
        backdropUrl = backdropPath?.let { "${TmdbApi.IMAGE_BASE_URL}$it" },
        rating = voteAverage,
        releaseDate = releaseDate,
        runtime = runtime,
        tagline = tagline,
        genres = genres?.map { it.name } ?: emptyList()
    )
}

fun ReviewResponse.toDomain(): Review {
    return Review(
        id = id,
        author = author,
        content = content,
        createdAt = createdAt,
        rating = authorDetails.rating
    )
}

fun VideoResponse.toDomain(): Video {
    return Video(
        id = id,
        key = key,
        name = name,
        site = site,
        type = type
    )
}
