package com.ahmed.cinema.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ahmed.cinema.R
import com.ahmed.cinema.util.Constants
import com.ahmed.cinema.util.RecentlyViewedMovie

@Composable
fun RecentlyViewedSection(
    recentlyViewed: List<RecentlyViewedMovie>,
    onMovieClick: (RecentlyViewedMovie) -> Unit
) {
    if (recentlyViewed.isEmpty()) return
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            stringResource(R.string.continue_watching),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(recentlyViewed.size) { index ->
                val movie = recentlyViewed[index]
                RecentlyViewedMovieCard(
                    movie = movie,
                    onMovieClick = { onMovieClick(movie) }
                )
            }
        }
    }
}

@Composable
fun RecentlyViewedMovieCard(
    movie: RecentlyViewedMovie,
    onMovieClick: () -> Unit
) {
    val context = LocalContext.current
    val posterUrl = movie.posterPath.takeIf { it.isNotBlank() }?.let { Constants.IMAGE_BASE_URL + it }
    
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onMovieClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(posterUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.movie_poster),
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            movie.title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            modifier = Modifier.width(120.dp)
        )
    }
}
