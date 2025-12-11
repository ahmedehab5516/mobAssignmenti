package com.ahmed.cinema.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.cinema.domain.model.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class FavoritesState {
    data object Loading : FavoritesState()
    data class Success(val movies: List<Movie>) : FavoritesState()
    data class Error(val message: String) : FavoritesState()
    data object Empty : FavoritesState()
}

@HiltViewModel
class FavoritesViewModel @Inject constructor() : ViewModel() {

    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.Loading)
    val favoritesState: StateFlow<FavoritesState> = _favoritesState.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                _favoritesState.value = FavoritesState.Loading
                val uid = auth.currentUser?.uid ?: run {
                    _favoritesState.value = FavoritesState.Empty
                    return@launch
                }
                
                val snapshot = firestore.collection("users").document(uid)
                    .collection("favorites").get().await()

                val movies = snapshot.documents.mapNotNull { doc ->
                    try {
                        Movie(
                            id = doc.getLong("movieId")?.toInt() ?: return@mapNotNull null,
                            title = doc.getString("title") ?: "",
                            overview = doc.getString("overview") ?: "",
                            posterPath = doc.getString("posterPath") ?: "",
                            releaseDate = doc.getString("releaseDate") ?: "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            genreIds = emptyList()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                if (movies.isEmpty()) {
                    _favoritesState.value = FavoritesState.Empty
                } else {
                    _favoritesState.value = FavoritesState.Success(movies)
                }
            } catch (e: Exception) {
                _favoritesState.value = FavoritesState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun removeFavorite(movieId: Int) {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                firestore.collection("users").document(uid)
                    .collection("favorites").document(movieId.toString()).delete().await()
                loadFavorites()
            } catch (e: Exception) {
                _favoritesState.value = FavoritesState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
