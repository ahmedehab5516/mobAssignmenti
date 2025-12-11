package com.ahmed.cinema.presentation.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.cinema.domain.model.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor() : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun checkIfFavorite(movieId: Int) {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val doc = firestore.collection("users").document(uid)
                    .collection("favorites").document(movieId.toString()).get().await()
                _isFavorite.value = doc.exists()
            } catch (e: Exception) {
                _isFavorite.value = false
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val docRef = firestore.collection("users").document(uid)
                    .collection("favorites").document(movie.id.toString())

                if (_isFavorite.value) {
                    // Remove from favorites
                    docRef.delete().await()
                    _isFavorite.value = false
                } else {
                    // Add to favorites
                    docRef.set(
                        mapOf(
                            "movieId" to movie.id,
                            "title" to movie.title,
                            "posterPath" to movie.posterPath,
                            "rating" to movie.rating,
                            "releaseDate" to movie.releaseDate,
                            "overview" to movie.overview,
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                    ).await()
                    _isFavorite.value = true
                }
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
}
