package com.ahmed.cinema.presentation.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.ahmed.cinema.R
import com.ahmed.cinema.presentation.moviedetail.MovieDetailActivity
import com.ahmed.cinema.presentation.common.TopAppBarWithUser
import com.ahmed.cinema.presentation.favorites.FavoritesViewModel
import com.ahmed.cinema.ui.theme.CinemaTheme
import com.ahmed.cinema.util.AppPreferences
import com.ahmed.cinema.util.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply saved language
        lifecycleScope.launch {
            AppPreferences.getLanguageFlow(this@MainActivity).collect { language ->
                setAppLanguage(language)
            }
        }
        
        setContent {
            val isDarkMode by AppPreferences.getDarkModeFlow(this).collectAsState(initial = false)
            
            CinemaTheme(darkTheme = isDarkMode) {
                MainScreen()
            }
        }
    }
    
    private fun setAppLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

@Composable
fun MainScreen(viewModel: HomeViewModel = hiltViewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBarWithUser(
                title = "Cinema",
                onSettingsClick = { selectedTab = 3 },
                showSettingsIcon = selectedTab < 3
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home)) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = stringResource(R.string.favorites)) },
                    label = { Text(stringResource(R.string.favorites)) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = stringResource(R.string.my_tickets)) },
                    label = { Text(stringResource(R.string.my_tickets)) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeTab(viewModel, Modifier.padding(innerPadding)) { movie ->
                val intent = Intent(context, MovieDetailActivity::class.java).apply {
                    putExtra("movieId", movie.id)
                    putExtra("movieTitle", movie.title)
                    putExtra("movieOverview", movie.overview)
                    putExtra("moviePosterPath", movie.posterPath)
                    putExtra("movieRating", movie.rating)
                    putExtra("movieReleaseDate", movie.releaseDate)
                }
                context.startActivity(intent)
            }
            1 -> FavoritesTab(Modifier.padding(innerPadding)) { movie ->
                val intent = Intent(context, MovieDetailActivity::class.java).apply {
                    putExtra("movieId", movie.id)
                    putExtra("movieTitle", movie.title)
                    putExtra("movieOverview", movie.overview)
                    putExtra("moviePosterPath", movie.posterPath)
                    putExtra("movieRating", movie.rating)
                    putExtra("movieReleaseDate", movie.releaseDate)
                }
                context.startActivity(intent)
            }
            2 -> com.ahmed.cinema.presentation.tickets.MyTicketsScreen()
            3 -> SettingsTab(Modifier.padding(innerPadding), context as? ComponentActivity)
        }
    }
}

@Composable
fun HomeTab(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onMovieClick: (com.ahmed.cinema.domain.model.Movie) -> Unit = {}
) {
    val nowPlayingState by viewModel.nowPlayingState.collectAsState()
    val upcomingState by viewModel.upcomingState.collectAsState()
    val context = LocalContext.current
    val recentlyViewed by com.ahmed.cinema.util.RecentlyViewedManager.getRecentlyViewedFlow(context).collectAsState(initial = emptyList())
    
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
            stringResource(R.string.cinema_booking),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Recently Viewed Section
        if (recentlyViewed.isNotEmpty()) {
            RecentlyViewedSection(
                recentlyViewed = recentlyViewed,
                onMovieClick = { movie ->
                    val intent = Intent(context, MovieDetailActivity::class.java).apply {
                        putExtra("movieId", movie.id)
                        putExtra("movieTitle", movie.title)
                        putExtra("moviePosterPath", movie.posterPath)
                    }
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        Text(
            stringResource(R.string.now_showing),
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
                MovieCarousel((nowPlayingState as MovieListState.Success).movies, onMovieClick)
            }
            is MovieListState.Error -> {
                Text("Error loading movies")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            stringResource(R.string.coming_soon),
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
                MovieCarousel((upcomingState as MovieListState.Success).movies, onMovieClick)
            }
            is MovieListState.Error -> {
                Text("Error loading movies")
            }
        }
    }
}

@Composable
fun FavoritesTab(modifier: Modifier = Modifier, onMovieClick: (com.ahmed.cinema.domain.model.Movie) -> Unit = {}) {
    val viewModel: com.ahmed.cinema.presentation.favorites.FavoritesViewModel = hiltViewModel()
    val favoritesState by viewModel.favoritesState.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }
    
    when (val state = favoritesState) {
        is com.ahmed.cinema.presentation.favorites.FavoritesState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is com.ahmed.cinema.presentation.favorites.FavoritesState.Success -> {
            Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.movies.size) { index ->
                        val movie = state.movies[index]
                        FavoriteMovieCard(
                            movie,
                            onMovieClick = {
                                val intent = Intent(context, MovieDetailActivity::class.java).apply {
                                    putExtra("movieId", movie.id)
                                    putExtra("movieTitle", movie.title)
                                    putExtra("movieOverview", movie.overview)
                                    putExtra("moviePosterPath", movie.posterPath)
                                    putExtra("movieRating", movie.rating)
                                    putExtra("movieReleaseDate", movie.releaseDate)
                                }
                                context.startActivity(intent)
                            },
                            onRemove = { viewModel.removeFavorite(movie.id) }
                        )
                    }
                }
            }
        }
        is com.ahmed.cinema.presentation.favorites.FavoritesState.Empty -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.no_favorites_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        is com.ahmed.cinema.presentation.favorites.FavoritesState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text("Error loading favorites")
            }
        }
    }
}

@Composable
fun FavoriteMovieCard(
    movie: com.ahmed.cinema.domain.model.Movie,
    onMovieClick: (com.ahmed.cinema.domain.model.Movie) -> Unit,
    onRemove: () -> Unit
) {
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
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onMovieClick(movie) },
                    onLongPress = { onRemove() }
                )
            }
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
            Text("★", color = MaterialTheme.colorScheme.primary)
            Text(
                " ${movie.rating}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun SettingsTab(modifier: Modifier = Modifier, activity: ComponentActivity? = null) {
    val context = LocalContext.current
    val language by AppPreferences.getLanguageFlow(context).collectAsState(initial = "en")
    val isDarkMode by AppPreferences.getDarkModeFlow(context).collectAsState(initial = false)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Language Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Language",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    if (language == "ar") "العربية" else "English",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Button(
                onClick = {
                    val newLanguage = if (language == "en") "ar" else "en"
                    (activity as? MainActivity)?.lifecycleScope?.launch {
                        AppPreferences.setLanguage(context, newLanguage)
                        activity?.recreate()
                    }
                },
                modifier = Modifier.width(80.dp)
            ) {
                Text(if (language == "en") "عربي" else "EN")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dark Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Dark Mode",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    if (isDarkMode) "On" else "Off",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = { isChecked ->
                    (activity as? MainActivity)?.lifecycleScope?.launch {
                        AppPreferences.setDarkMode(context, isChecked)
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Logout Button
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(context, com.ahmed.cinema.presentation.auth.LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
                activity?.finish()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.logout))
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Text(
            stringResource(R.string.app_version),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MovieCarousel(
    movies: List<com.ahmed.cinema.domain.model.Movie>,
    onMovieClick: (com.ahmed.cinema.domain.model.Movie) -> Unit = {}
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies.size) { index ->
            MovieCard(movies[index], onMovieClick)
        }
    }
}

@Composable
fun MovieCard(
    movie: com.ahmed.cinema.domain.model.Movie,
    onMovieClick: (com.ahmed.cinema.domain.model.Movie) -> Unit = {}
) {
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
            .clickable { onMovieClick(movie) }
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
