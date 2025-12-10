// api/MovieMapper.kt
package com.ahmed.assignment.api

import com.ahmed.assignment.data.Movie

object MovieMapper {
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342"  // Poster size
    private const val GENRE_API_URL = "https://api.themoviedb.org/3/genre/movie/list?api_key=YOUR_KEY&language=en-US"

    // Map TMDB DTO to your Movie model
    fun toMovie(dto: MovieDto, isNowShowing: Boolean): Movie {
        val posterUrl = dto.poster_path?.let { IMAGE_BASE_URL + it }
        val genres = mapOf(  // Hardcoded common genres (or fetch dynamically later)
            28 to "Action", 12 to "Adventure", 16 to "Animation",
            35 to "Comedy", 80 to "Crime", 99 to "Documentary",
            18 to "Drama", 10751 to "Family", 14 to "Fantasy",
            36 to "History", 27 to "Horror", 10402 to "Music",
            9648 to "Mystery", 10749 to "Romance", 878 to "Sci-Fi",
            10770 to "TV Movie", 53 to "Thriller", 10752 to "War",
            37 to "Western"
        )
        val genreNames = dto.genre_ids.take(3).mapNotNull { genres[it] }.joinToString(" • ")
        val duration = dto.runtime?.let { "${it / 60}h ${it % 60}m" } ?: "TBD"

        return Movie(
            id = dto.id,
            title = dto.title,
            genre = if (genreNames.isEmpty()) "Unknown" else genreNames,
            duration = duration,
            rating = dto.vote_average,
            posterUrl = posterUrl,
            isNowShowing = isNowShowing,
            overview = dto.overview,
            releaseDate = dto.release_date
        )
    }
}