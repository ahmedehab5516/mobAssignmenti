package com.ahmed.cinema.presentation.seatbooking

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.cinema.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val DEFAULT_ROWS = 10
private const val DEFAULT_COLS = 10

data class SeatUi(
    val id: String,
    val booked: Boolean,
    val userId: String?
)

data class SeatSelectionUiState(
    val seats: List<SeatUi> = emptyList(),
    val selectedSeatIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isBooking: Boolean = false,
    val error: String? = null,
    val bookedSeats: List<String> = emptyList()
)

sealed class SeatSelectionEvent {
    data class ShowMessage(@androidx.annotation.StringRes val messageId: Int, val arg: String? = null) : SeatSelectionEvent()
    data class BookingSuccess(val seats: List<String>) : SeatSelectionEvent()
}

class BookingConflict(val seatId: String) : Exception()

@HiltViewModel
class SeatSelectionViewModel @Inject constructor() : ViewModel() {
    private val tag = "SeatBookingVM"
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val _uiState = MutableStateFlow(SeatSelectionUiState())
    val uiState: StateFlow<SeatSelectionUiState> = _uiState
    
    private val _events = Channel<SeatSelectionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    private var movieId: Int = 0
    private var showtimeId: String = "default"
    private var listenerRegistered = false
    
    fun start(movieId: Int, showtimeId: String) {
        if (listenerRegistered && this.movieId == movieId && this.showtimeId == showtimeId) return
        this.movieId = movieId
        this.showtimeId = showtimeId
        listenerRegistered = true
        Log.d(tag, "start() movieId=$movieId showtimeId=$showtimeId")
        ensureSeatsExist()
        listenToSeats()
    }
    
    private fun ensureSeatsExist() {
        val seatsRef = firestore.collection("movies")
            .document(movieId.toString())
            .collection("showtimes")
            .document(showtimeId)
            .collection("seats")
        seatsRef.limit(1).get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) return@addOnSuccessListener
            Log.d(tag, "ensureSeatsExist(): seeding seats for movie=$movieId showtime=$showtimeId")
            val batch = firestore.batch()
            for (row in 0 until DEFAULT_ROWS) {
                for (col in 0 until DEFAULT_COLS) {
                    val seatId = seatId(row, col)
                    val doc = seatsRef.document(seatId)
                    batch.set(doc, mapOf("booked" to false, "userId" to ""))
                }
            }
            batch.commit()
        }
    }
    
    private fun listenToSeats() {
        val seatsRef = firestore.collection("movies")
            .document(movieId.toString())
            .collection("showtimes")
            .document(showtimeId)
            .collection("seats")
        seatsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(tag, "listenToSeats error", error)
                _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                return@addSnapshotListener
            }
            val seats = snapshot?.documents?.map { doc ->
                SeatUi(
                    id = doc.id,
                    booked = doc.getBoolean("booked") == true,
                    userId = doc.getString("userId")
                )
            }?.sortedBy { it.id } ?: emptyList()
            Log.d(tag, "listenToSeats(): seats=${seats.size}")
            _uiState.value = _uiState.value.copy(isLoading = false, seats = seats)
        }
    }
    
    fun toggleSeat(seatId: String) {
        val current = _uiState.value
        val seat = current.seats.find { it.id == seatId } ?: return
        if (seat.booked) return
        val updated = current.selectedSeatIds.toMutableSet()
        if (!updated.add(seatId)) {
            updated.remove(seatId)
        }
        _uiState.value = current.copy(selectedSeatIds = updated)
        Log.d(tag, "toggleSeat(): seat=$seatId selected=${updated.contains(seatId)} totalSelected=${updated.size}")
    }
    
    fun bookSeats(movieTitle: String) {
        val uid = auth.currentUser?.uid
        val selected = _uiState.value.selectedSeatIds
        if (uid == null) {
            viewModelScope.launch { _events.send(SeatSelectionEvent.ShowMessage(R.string.error_sign_in_required)) }
            return
        }
        if (selected.isEmpty() || _uiState.value.isBooking) return
        _uiState.value = _uiState.value.copy(isBooking = true, error = null)
        val seatsRef = firestore.collection("movies")
            .document(movieId.toString())
            .collection("showtimes")
            .document(showtimeId)
            .collection("seats")
        viewModelScope.launch {
            try {
                Log.d(tag, "bookSeats(): movieId=$movieId showtimeId=$showtimeId seats=$selected uid=$uid")
                firestore.runTransaction { transaction ->
                    val seatDocs = selected.map { seatId ->
                        seatId to transaction.get(seatsRef.document(seatId))
                    }
                    seatDocs.forEach { (seatId, snap) ->
                        if (snap.exists() && snap.getBoolean("booked") == true) {
                            Log.w(tag, "bookSeats(): seat already booked -> $seatId")
                            throw BookingConflict(seatId)
                        }
                    }
                    seatDocs.forEach { (seatId, _) ->
                        val docRef = seatsRef.document(seatId)
                        transaction.set(docRef, mapOf("booked" to true, "userId" to uid), SetOptions.merge())
                    }
                    val bookingRef = firestore.collection("users")
                        .document(uid)
                        .collection("bookings")
                        .document()
                    transaction.set(bookingRef, mapOf(
                        "movieId" to movieId,
                        "showtimeId" to showtimeId,
                        "seats" to selected.toList(),
                        "movieTitle" to movieTitle,
                        "timestamp" to System.currentTimeMillis()
                    ))
                }.await()
                Log.d(tag, "bookSeats(): transaction success seats=${selected.size}")
                _uiState.value = _uiState.value.copy(
                    isBooking = false,
                    bookedSeats = selected.toList(),
                    selectedSeatIds = emptySet()
                )
                _events.send(SeatSelectionEvent.BookingSuccess(selected.toList()))
            } catch (e: Exception) {
                Log.e(tag, "bookSeats() failed", e)
                _uiState.value = _uiState.value.copy(isBooking = false, error = e.message)
                val event = when (e) {
                    is BookingConflict -> SeatSelectionEvent.ShowMessage(R.string.error_seat_taken, e.seatId)
                    else -> SeatSelectionEvent.ShowMessage(R.string.error_booking_failed)
                }
                _events.send(event)
            }
        }
    }
    
    private fun seatId(row: Int, col: Int): String {
        val letter = 'A' + row
        return "$letter${col + 1}"
    }
}
