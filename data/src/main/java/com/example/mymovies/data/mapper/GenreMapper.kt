package com.example.mymovies.data.mapper

import com.example.mymovies.core.network.model.GenreResponse
import com.example.mymovies.domain.model.Genre

fun GenreResponse.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}
