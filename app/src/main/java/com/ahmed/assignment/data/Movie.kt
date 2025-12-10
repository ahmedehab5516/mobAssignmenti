// data/Movie.kt
package com.ahmed.assignment.data

data class Movie(
    val id: Int,                          // TMDB ID (use as String? if needed)
    val title: String,
    val genre: String,                    // We'll join genres
    val duration: String,                 // e.g., "148 min"
    val rating: Double,                   // TMDB vote_average / 2 for IMDb-like (out of 10)
    val posterUrl: String?,               // Full TMDB image URL
    val isNowShowing: Boolean = true,
    val overview: String = "",            // Short description
    val releaseDate: String = ""          // For sorting/filtering
)