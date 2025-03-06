package com.example.flickpicks.ui.screens

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flickpicks.R
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.ui.viewmodels.MainViewModel
import com.example.flickpicks.ui.viewmodels.MyFeedViewModel

// Mock MovieReview Data
val mockReviews = listOf(
    MovieReview(
        id = 1,
        movieId = "27205", // TMDB ID for Inception
        movieTitle = "Inception",
        release_date = "2010-07-16",
        tagline = "Your mind is the scene of the crime.",
        overview = "A mind-bending thriller.",
        genres = listOf("Sci-Fi", "Thriller"),
        reviewerName = "Alice",
        reviewText = "Amazing visuals and storytelling!",
        rating = 5,
        streamingPlatform = "Netflix"
    ),
    MovieReview(
        id = 2,
        movieId = "157336", // TMDB ID for Interstellar
        movieTitle = "Interstellar",
        release_date = "2014-11-07",
        tagline = "Mankind was born on Earth. It was never meant to die here.",
        overview = "Exploring space and time.",
        genres = listOf("Sci-Fi", "Adventure"),
        reviewerName = "Bob",
        reviewText = "Mind-blowing space exploration!",
        rating = 5,
        streamingPlatform = "Amazon Prime"
    ),
    MovieReview(
        id = 3,
        movieId = "496243", // TMDB ID for Parasite
        movieTitle = "Parasite",
        release_date = "2019-05-30",
        tagline = "Act like you own the place.",
        overview = "A sharp social thriller.",
        genres = listOf("Drama", "Thriller"),
        reviewerName = "Charlie",
        reviewText = "Brilliant social commentary!",
        rating = 5,
        streamingPlatform = "Hulu"
    ),
    MovieReview(
        id = 4,
        movieId = "155", // TMDB ID for The Dark Knight
        movieTitle = "The Dark Knight",
        release_date = "2008-07-18",
        tagline = "Why so serious?",
        overview = "The greatest superhero film.",
        genres = listOf("Action", "Crime"),
        reviewerName = "David",
        reviewText = "Best Batman movie ever!",
        rating = 5,
        streamingPlatform = "HBO Max"
    ),
    MovieReview(
        id = 5,
        movieId = "438631", // TMDB ID for Dune (2021)
        movieTitle = "Dune",
        release_date = "2021-10-22",
        tagline = "Beyond fear, destiny awaits.",
        overview = "A futuristic sci-fi epic.",
        genres = listOf("Sci-Fi", "Adventure"),
        reviewerName = "Eve",
        reviewText = "Stunning cinematography!",
        rating = 4,
        streamingPlatform = "Disney+"
    )
)

@SuppressLint("ContextCastToActivity")
@Composable
fun MyFeed(
    navController: NavController,
    viewModel: MyFeedViewModel = hiltViewModel()
) {
    var selectedFilter by remember { mutableStateOf("Trending") }
    val filters = listOf("Trending", "Reviewed By Friends", "Recommendations")
    val trendingMovies by viewModel.trendingMovies
    val reviewedByFriends by viewModel.moviesReviewedByFriends
    val recommendations by viewModel.recommendedMovies

    val mainViewModel = viewModel<MainViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
    val currentUser = mainViewModel.getCurrentUser()

    LaunchedEffect(Unit) {
        if (currentUser != null) {
            viewModel.fetchReviewedByFriends(currentUser.id)
        }
    }

    LaunchedEffect(Unit) {
        if (currentUser != null) {
            viewModel.fetchRecommendedMovies(currentUser.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Feed",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

        // Top Navigation Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            items(filters) { filter ->
                Button(
                    onClick = { selectedFilter = filter },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (selectedFilter == filter) Color.White else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (selectedFilter) {
            "Trending" -> {
                TrendingFeedScreen(navController, trendingMovies)
            }
            "Reviewed By Friends" -> {
                FeedScreen(navController, mockReviews)
            }
            else -> {
                FeedScreen(navController, mockReviews)
            }
        }
    }
}

@Composable
fun TrendingFeedScreen(navController: NavController, movies: List<Movie>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movies) { movie ->
            TrendingMovieItem(movie, onClick = {
                navController.navigate(Screens.MovieDetail.createRoute(movie.id))
            })
        }
    }
}

@Composable
fun FeedScreen(navController: NavController, reviews: List<MovieReview>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(reviews) { review ->
            MovieItem(review, onClick = {
                navController.navigate(Screens.MovieDetail.createRoute(review.movieId))
            })
        }
    }
}

@Composable
fun TrendingMovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = movie.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Release Date: ${movie.release_date}", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Overview: ${movie.overview}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Rating: ⭐ ${movie.vote_average}", style = MaterialTheme.typography.labelMedium)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Love"
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = "Like"
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_thumbs_down),
                    contentDescription = "Dislike"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { }) { Text("Save") }
                TextButton(onClick = { }) { Text("Watch Now") }
            }
        }
    }
}

@Composable
fun MovieItem(movie: MovieReview, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = movie.movieTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Reviewed by: ${movie.reviewerName}", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Review: ${movie.reviewText}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Rating: ⭐ ${movie.rating}/5", style = MaterialTheme.typography.labelMedium)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Love"
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = "Like"
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_thumbs_down),
                    contentDescription = "Dislike"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { }) { Text("Save") }
                TextButton(onClick = { }) { Text("Watch Now") }
            }
        }
    }
}
