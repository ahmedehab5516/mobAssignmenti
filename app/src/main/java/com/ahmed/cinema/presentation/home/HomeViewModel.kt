package com.ahmed.cinema.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.cinema.domain.model.Movie
import com.ahmed.cinema.domain.usecase.GetNowPlayingMoviesUseCase
import com.ahmed.cinema.domain.usecase.GetUpcomingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MovieListState {
    data object Loading : MovieListState()
    data class Success(val movies: List<Movie>) : MovieListState()
    data class Error(val message: String) : MovieListState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase
) : ViewModel() {

    private val _nowPlayingState = MutableStateFlow<MovieListState>(MovieListState.Loading)
    val nowPlayingState: StateFlow<MovieListState> = _nowPlayingState.asStateFlow()

    private val _upcomingState = MutableStateFlow<MovieListState>(MovieListState.Loading)
    val upcomingState: StateFlow<MovieListState> = _upcomingState.asStateFlow()

    fun loadNowPlayingMovies() {
        viewModelScope.launch {
            getNowPlayingMoviesUseCase().collect { result ->
                _nowPlayingState.value = result.fold(
                    onSuccess = { MovieListState.Success(it) },
                    onFailure = { MovieListState.Error(it.message ?: "Unknown error") }
                )
            }
        }
    }

    fun loadUpcomingMovies() {
        viewModelScope.launch {
            getUpcomingMoviesUseCase().collect { result ->
                _upcomingState.value = result.fold(
                    onSuccess = { MovieListState.Success(it) },
                    onFailure = { MovieListState.Error(it.message ?: "Unknown error") }
                )
            }
        }
    }
}
