package com.ahmed.cinema.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val rating: Double,
    val genreIds: List<Int>,
    val runtime: Int = 0
)

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val rating: Double,
    val genres: List<Genre>,
    val runtime: Int,
    val backdropPath: String
)

data class Genre(
    val id: Int,
    val name: String
)
