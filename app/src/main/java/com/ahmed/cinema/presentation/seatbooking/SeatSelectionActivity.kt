package com.ahmed.cinema.presentation.seatbooking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ahmed.cinema.ui.theme.CinemaTheme
import com.ahmed.cinema.presentation.seatbooking.BookingConfirmationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeatSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieId = intent.getIntExtra(EXTRA_MOVIE_ID, 0)
        val movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE) ?: ""
        val showtimeId = intent.getStringExtra(EXTRA_SHOWTIME_ID) ?: "default"
        setContent {
            CinemaTheme {
                SeatSelectionScreen(
                    movieId = movieId,
                    movieTitle = movieTitle,
                    showtimeId = showtimeId,
                    onBack = { finish() },
                    onBooked = { seats ->
                        startActivity(BookingConfirmationActivity.newIntent(this, movieTitle, seats))
                        finish()
                    }
                )
            }
        }
    }
    
    companion object {
        private const val EXTRA_MOVIE_ID = "extra_movie_id"
        private const val EXTRA_MOVIE_TITLE = "extra_movie_title"
        private const val EXTRA_SHOWTIME_ID = "extra_showtime_id"
        
        fun newIntent(context: Context, movieId: Int, movieTitle: String, showtimeId: String = "default"): Intent {
            return Intent(context, SeatSelectionActivity::class.java).apply {
                putExtra(EXTRA_MOVIE_ID, movieId)
                putExtra(EXTRA_MOVIE_TITLE, movieTitle)
                putExtra(EXTRA_SHOWTIME_ID, showtimeId)
            }
        }
    }
}
