package com.ahmed.cinema.domain.usecase

import com.ahmed.cinema.data.repository.IMovieRepository
import com.ahmed.cinema.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNowPlayingMoviesUseCase @Inject constructor(
    private val repository: IMovieRepository
) {
    operator fun invoke(): Flow<Result<List<Movie>>> = repository.getNowPlayingMovies()
}

class GetUpcomingMoviesUseCase @Inject constructor(
    private val repository: IMovieRepository
) {
    operator fun invoke(): Flow<Result<List<Movie>>> = repository.getUpcomingMovies()
}

class GetMovieDetailUseCase @Inject constructor(
    private val repository: IMovieRepository
) {
    operator fun invoke(movieId: Int) = repository.getMovieDetail(movieId)
}

class SearchMoviesUseCase @Inject constructor(
    private val repository: IMovieRepository
) {
    operator fun invoke(query: String) = repository.searchMovies(query)
}
