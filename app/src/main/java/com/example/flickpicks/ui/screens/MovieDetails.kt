package com.example.flickpicks.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.ui.viewmodels.MyFeedViewModel

@Composable
fun MovieDetailScreen(
    movieId: String,
    navController: NavController,
    viewModel: MyFeedViewModel = hiltViewModel()
) {

    val selectedMovie by viewModel.selectedMovie
    val watchProviders by viewModel.watchProviders

    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(movieId)
    }

    LaunchedEffect(movieId) {
        viewModel.fetchWatchProviders(movieId)
    }

    var selectedTab by remember { mutableStateOf("Overview") }
    val tabs = listOf("Overview", "Reviews", "Where to Watch", "Add Review")

    if (selectedMovie == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${selectedMovie!!.poster_path}"), // Replace with movie poster URL
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(250.dp).background(Color.Gray)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = selectedMovie!!.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Released: ${selectedMovie!!.release_date}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Watch Trailer")
                    }
                }
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items(tabs) { tab ->
                Button(
                    onClick = { selectedTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(text = tab, color = if (selectedTab == tab) Color.White else Color.Black)
                }
            }
        }

        when (selectedTab) {
            "Overview" -> OverviewTab(selectedMovie!!)
            "Reviews" -> ReviewsTab(movieId, mockReviews)
            "Where to Watch" -> WhereToWatchTab(watchProviders)
            "Add Review" -> AddReviewTab(movieId)
        }
    }
}

@Composable
fun OverviewTab(movie: Movie) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Tagline: ${movie.tagline}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Overview: ${movie.overview}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Genres: ${movie.genres.joinToString(", ")}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Save to Wishlist")
        }
    }
}

@Composable
fun ReviewsTab(movieId: String, reviews: List<MovieReview>) {
    val movieReviews = reviews.filter { it.movieId == movieId }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(movieReviews) { review ->
            Card(modifier = Modifier.padding(8.dp).fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "👤 ${review.reviewerName}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "⭐ ${review.rating}/5", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = review.reviewText, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "👍 Liked", modifier = Modifier.clickable { })
                        // Text(text = "👎 Disliked", modifier = Modifier.clickable { })
                        // Text(text = "❤️ Loved", modifier = Modifier.clickable { })
                    }
                }
            }
        }
    }
}

@Composable
fun WhereToWatchTab(watchProviders: List<String>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = if (watchProviders.isNotEmpty())
                "Available on: ${watchProviders.joinToString(", ")}"
            else
                "Available on: Unknown",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}



@Composable
fun AddReviewTab(movieId: String) {
    var rating by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var streaming_platform by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Your Rating (1-5):")
        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Your Review:")
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Where did you watch the movie?")
        OutlinedTextField(
            value = streaming_platform,
            onValueChange = { streaming_platform = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Post Review")
        }
    }
}
