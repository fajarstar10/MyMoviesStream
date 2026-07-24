package com.example.mymovies.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val rating: Double,
    val releaseDate: String?,
    val runtime: Int? = null,
    val tagline: String? = null,
    val genres: List<String> = emptyList()
)
