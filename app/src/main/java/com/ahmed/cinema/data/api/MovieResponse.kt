package com.ahmed.cinema.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieResponse(
    val results: List<MovieDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "vote_average")
    val rating: Double,
    @Json(name = "genre_ids")
    val genreIds: List<Int> = emptyList(),
    val runtime: Int = 0
)

@JsonClass(generateAdapter = true)
data class MovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "backdrop_path")
    val backdropPath: String?,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "vote_average")
    val rating: Double,
    val genres: List<GenreDto> = emptyList(),
    val runtime: Int = 0
)

@JsonClass(generateAdapter = true)
data class GenreDto(
    val id: Int,
    val name: String
)

@JsonClass(generateAdapter = true)
data class GenreListResponse(
    val genres: List<GenreDto> = emptyList()
)
