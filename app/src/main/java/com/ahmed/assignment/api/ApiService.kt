package com.ahmed.assignment.api



import com.ahmed.assignment.data.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Now Playing Movies
    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<MoviesResponse>

    // Upcoming Movies (Coming Soon)
    @GET("movie/upcoming")
    suspend fun getUpcoming(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<MoviesResponse>
}

// TMDB Response Wrapper (for the API's JSON)
data class MoviesResponse(
    val results: List<MovieDto>
)

// DTO (Data Transfer Object) - Matches TMDB JSON exactly
data class MovieDto(
    val id: Int,
    val title: String,
    val genre_ids: List<Int>,
    val vote_average: Double,
    val poster_path: String?,
    val runtime: Int?,                    // In minutes (from /movie/{id})
    val overview: String,
    val release_date: String
)