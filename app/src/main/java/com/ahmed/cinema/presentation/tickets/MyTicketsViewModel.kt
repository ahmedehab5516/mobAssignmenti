package com.ahmed.cinema.presentation.tickets

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject


data class TicketItem(
    val id: String,
    val movieTitle: String,
    val showtimeId: String,
    val seats: List<String>,
    val timestamp: Long
)

data class MyTicketsUiState(
    val tickets: List<TicketItem> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MyTicketsViewModel @Inject constructor() : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(MyTicketsUiState())
    val uiState: StateFlow<MyTicketsUiState> = _uiState
    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()
    
    init {
        loadTickets()
    }
    
    private fun loadTickets() {
        val uid = auth.currentUser?.uid ?: run {
            _uiState.value = MyTicketsUiState(loading = false)
            return
        }
        firestore.collection("users")
            .document(uid)
            .collection("bookings")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = MyTicketsUiState(loading = false, error = error.message)
                    return@addSnapshotListener
                }
                val tickets = snapshot?.documents?.map { doc ->
                    TicketItem(
                        id = doc.id,
                        movieTitle = doc.getString("movieTitle") ?: "",
                        showtimeId = doc.getString("showtimeId") ?: "",
                        seats = (doc.get("seats") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                } ?: emptyList()
                _uiState.value = MyTicketsUiState(tickets = tickets, loading = false)
            }
    }
}
