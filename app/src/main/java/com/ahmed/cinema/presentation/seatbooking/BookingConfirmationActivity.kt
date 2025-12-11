package com.ahmed.cinema.presentation.seatbooking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ahmed.cinema.R
import com.ahmed.cinema.ui.theme.CinemaTheme

class BookingConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE) ?: ""
        val seats = intent.getStringArrayListExtra(EXTRA_SEATS) ?: arrayListOf()
        setContent {
            CinemaTheme {
                BookingConfirmationScreen(
                    movieTitle = movieTitle,
                    seats = seats,
                    onClose = { finish() }
                )
            }
        }
    }
    companion object {
        private const val EXTRA_MOVIE_TITLE = "extra_movie_title"
        private const val EXTRA_SEATS = "extra_seats"
        fun newIntent(context: Context, movieTitle: String, seats: List<String>): Intent {
            return Intent(context, BookingConfirmationActivity::class.java).apply {
                putExtra(EXTRA_MOVIE_TITLE, movieTitle)
                putStringArrayListExtra(EXTRA_SEATS, ArrayList(seats))
            }
        }
    }
}

@Composable
fun BookingConfirmationScreen(
    movieTitle: String,
    seats: List<String>,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.booking_confirmed),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = movieTitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = seats.joinToString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.back))
        }
    }
}
