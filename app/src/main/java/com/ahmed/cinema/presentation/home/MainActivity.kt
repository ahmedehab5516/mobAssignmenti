package com.ahmed.cinema.presentation.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.hilt.navigation.compose.hiltViewModel
import com.ahmed.cinema.R
import com.ahmed.cinema.ui.theme.CinemaTheme
import com.ahmed.cinema.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CinemaTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: HomeViewModel = hiltViewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeTab(viewModel, Modifier.padding(innerPadding))
            1 -> FavoritesTab(Modifier.padding(innerPadding))
            2 -> SettingsTab(Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun HomeTab(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val nowPlayingState by viewModel.nowPlayingState.collectAsState()
    val upcomingState by viewModel.upcomingState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadNowPlayingMovies()
        viewModel.loadUpcomingMovies()
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Cinema Booking",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Now Showing",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        when (nowPlayingState) {
            is MovieListState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MovieListState.Success -> {
                MovieCarousel((nowPlayingState as MovieListState.Success).movies)
            }
            is MovieListState.Error -> {
                Text("Error loading movies")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Coming Soon",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        when (upcomingState) {
            is MovieListState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MovieListState.Success -> {
                MovieCarousel((upcomingState as MovieListState.Success).movies)
            }
            is MovieListState.Error -> {
                Text("Error loading movies")
            }
        }
    }
}

@Composable
fun FavoritesTab(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No favorites yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SettingsTab(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { /* TODO: Logout */ }) {
            Text("Logout")
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "Cinema Booking v1.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MovieCarousel(movies: List<com.ahmed.cinema.domain.model.Movie>) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies.size) { index ->
            MovieCard(movies[index])
        }
    }
}

@Composable
fun MovieCard(movie: com.ahmed.cinema.domain.model.Movie) {
    val context = LocalContext.current
    val posterUrl = movie.posterPath.takeIf { it.isNotBlank() }?.let { Constants.IMAGE_BASE_URL + it }
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(posterUrl)
            .placeholder(R.drawable.movie_placeholder)
            .error(R.drawable.movie_placeholder)
            .crossfade(true)
            .build()
    )

    Column(
        modifier = Modifier
            .width(150.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.movie_poster),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            movie.title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "★",
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                " ${movie.rating}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
