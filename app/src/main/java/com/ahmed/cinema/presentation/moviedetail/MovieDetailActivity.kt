package com.ahmed.cinema.presentation.moviedetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.ahmed.cinema.R
import com.ahmed.cinema.domain.model.Movie
import com.ahmed.cinema.ui.theme.CinemaTheme
import com.ahmed.cinema.util.AppPreferences
import com.ahmed.cinema.util.Constants
import com.ahmed.cinema.presentation.seatbooking.SeatSelectionActivity
import com.ahmed.cinema.util.RecentlyViewedManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Log this movie as recently viewed
        val movieId = intent.getIntExtra("movieId", 0)
        val movieTitle = intent.getStringExtra("movieTitle") ?: ""
        val moviePosterPath = intent.getStringExtra("moviePosterPath") ?: ""
        
        if (movieId > 0) {
            lifecycleScope.launch {
                RecentlyViewedManager.addRecentlyViewed(
                    this@MovieDetailActivity,
                    movieId,
                    movieTitle,
                    moviePosterPath
                )
            }
        }
        
        setContent {
            val isDarkMode by AppPreferences.getDarkModeFlow(this@MovieDetailActivity).collectAsState(initial = false)
            
            CinemaTheme(darkTheme = isDarkMode) {
                MovieDetailScreen(onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun MovieDetailScreen(
    onBackClick: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isFavorite by viewModel.isFavorite.collectAsState()

    val movieId = (context as? MovieDetailActivity)?.intent?.getIntExtra("movieId", 0) ?: 0
    val movieTitle = (context as? MovieDetailActivity)?.intent?.getStringExtra("movieTitle") ?: ""
    val movieOverview = (context as? MovieDetailActivity)?.intent?.getStringExtra("movieOverview") ?: ""
    val moviePosterPath = (context as? MovieDetailActivity)?.intent?.getStringExtra("moviePosterPath") ?: ""
    val movieRating = (context as? MovieDetailActivity)?.intent?.getDoubleExtra("movieRating", 0.0) ?: 0.0
    val movieReleaseDate = (context as? MovieDetailActivity)?.intent?.getStringExtra("movieReleaseDate") ?: ""

    val movie = Movie(
        id = movieId,
        title = movieTitle,
        overview = movieOverview,
        posterPath = moviePosterPath,
        releaseDate = movieReleaseDate,
        rating = movieRating,
        genreIds = emptyList()
    )

    LaunchedEffect(movieId) {
        viewModel.checkIfFavorite(movieId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar with back and favorite buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.toggleFavorite(movie) }) {
                val icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                val tint = animateColorAsState(
                    if (isFavorite) Color.Red else MaterialTheme.colorScheme.onBackground,
                    label = "favorite_color"
                ).value
                Icon(
                    icon,
                    contentDescription = stringResource(if (isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites),
                    tint = tint
                )
            }
        }

        // Poster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val posterUrl = moviePosterPath.takeIf { it.isNotBlank() }
                ?.let { Constants.IMAGE_BASE_URL + it }
            val painter = rememberAsyncImagePainter(
                model = posterUrl,
                placeholder = painterResource(R.drawable.movie_placeholder),
                error = painterResource(R.drawable.movie_placeholder)
            )
            Image(
                painter = painter,
                contentDescription = "Movie poster",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Content
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                movieTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("★", fontSize = MaterialTheme.typography.bodyMedium.fontSize)
                Text(
                    " ${"%.1f".format(movieRating)} (${movieReleaseDate.take(4)})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Overview",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                movieOverview.takeIf { it.isNotEmpty() } ?: "No overview available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val intent = com.ahmed.cinema.presentation.seatbooking.SeatSelectionActivity.newIntent(
                        context,
                        movieId,
                        movieTitle,
                        movieReleaseDate.ifBlank { "default" }
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.book_now))
            }
        }
    }
}
