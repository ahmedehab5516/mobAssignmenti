package com.ahmed.cinema.data.repository

import com.ahmed.cinema.data.api.MovieApiService
import com.ahmed.cinema.domain.model.Genre
import com.ahmed.cinema.domain.model.Movie
import com.ahmed.cinema.domain.model.MovieDetail
import com.ahmed.cinema.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface IMovieRepository {
    fun getNowPlayingMovies(): Flow<Result<List<Movie>>>
    fun getUpcomingMovies(): Flow<Result<List<Movie>>>
    fun getMovieDetail(movieId: Int): Flow<Result<MovieDetail>>
    fun searchMovies(query: String): Flow<Result<List<Movie>>>
}

class MovieRepository @Inject constructor(
    private val apiService: MovieApiService
) : IMovieRepository {

    override fun getNowPlayingMovies(): Flow<Result<List<Movie>>> = flow {
        try {
            val response = apiService.getNowPlaying(Constants.API_KEY)
            val movies = response.results.map { it.toDomain() }
            emit(Result.success(movies))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getUpcomingMovies(): Flow<Result<List<Movie>>> = flow {
        try {
            val response = apiService.getUpcoming(Constants.API_KEY)
            val movies = response.results.map { it.toDomain() }
            emit(Result.success(movies))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getMovieDetail(movieId: Int): Flow<Result<MovieDetail>> = flow {
        try {
            val response = apiService.getMovieDetail(movieId, Constants.API_KEY)
            emit(Result.success(response.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun searchMovies(query: String): Flow<Result<List<Movie>>> = flow {
        try {
            val response = apiService.searchMovies(query, Constants.API_KEY)
            val movies = response.results.map { it.toDomain() }
            emit(Result.success(movies))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

private fun com.ahmed.cinema.data.api.MovieDto.toDomain() = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath.orEmpty(),
    releaseDate = releaseDate,
    rating = rating,
    genreIds = genreIds,
    runtime = runtime
)

private fun com.ahmed.cinema.data.api.MovieDetailDto.toDomain() = MovieDetail(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath.orEmpty(),
    releaseDate = releaseDate,
    rating = rating,
    genres = genres.map { Genre(it.id, it.name) },
    runtime = runtime,
    backdropPath = backdropPath.orEmpty()
)
