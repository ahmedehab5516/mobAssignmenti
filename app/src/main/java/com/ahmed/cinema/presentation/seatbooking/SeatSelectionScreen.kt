package com.ahmed.cinema.presentation.seatbooking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import android.util.Log
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    movieId: Int,
    movieTitle: String,
    showtimeId: String = "default",
    onBack: () -> Unit,
    onBooked: (List<String>) -> Unit,
    viewModel: SeatSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = androidx.compose.material3.SnackbarHostState()
    val tag = "SeatBookingUI"
    
    LaunchedEffect(movieId, showtimeId) {
        viewModel.start(movieId, showtimeId)
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SeatSelectionEvent.BookingSuccess -> {
                    Log.d(tag, "BookingSuccess seats=${event.seats}")
                    onBooked(event.seats)
                }
                is SeatSelectionEvent.ShowMessage -> {
                    Log.w(tag, "ShowMessage: ${event.message}")
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SmallTopAppBar(
                title = { Text(text = movieTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = com.ahmed.cinema.R.string.seat_selection),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = showtimeId,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                SeatGrid(
                    seats = uiState.seats,
                    selected = uiState.selectedSeatIds,
                    onSeatClick = viewModel::toggleSeat
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.bookSeats(movieTitle) },
                    enabled = uiState.selectedSeatIds.isNotEmpty() && !uiState.isBooking,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isBooking) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text = stringResource(id = com.ahmed.cinema.R.string.book_now))
                }
                uiState.error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun SeatGrid(
    seats: List<SeatUi>,
    selected: Set<String>,
    onSeatClick: (String) -> Unit
) {
    val sorted = seats.sortedBy { it.id }
    LazyVerticalGrid(
        columns = GridCells.Fixed(10),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(max = 400.dp)
    ) {
        items(sorted) { seat ->
            val isSelected = selected.contains(seat.id)
            val background = when {
                seat.booked -> Color(0xFFD32F2F)
                isSelected -> Color(0xFF1976D2)
                else -> Color(0xFF2E7D32)
            }
            SeatBox(
                id = seat.id,
                background = background,
                onClick = { if (!seat.booked) onSeatClick(seat.id) }
            )
        }
    }
}

@Composable
private fun SeatBox(id: String, background: Color, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .background(background, shape = MaterialTheme.shapes.small)
            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small)
            .clickable { onClick() }
    ) {
        Text(
            text = id,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
