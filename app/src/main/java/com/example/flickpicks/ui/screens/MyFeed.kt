package com.example.flickpicks.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.navigation.NavController
import com.example.flickpicks.R
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.ui.viewmodels.MyFeedViewModel
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("ContextCastToActivity")
@Composable
fun MyFeed(
    navController: NavController,
    viewModel: MyFeedViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    var selectedFilter by remember { mutableStateOf("Trending") }
    val filters = listOf("Trending", "Recommendations")
    val reviewedFilter = listOf("Reviewed By Friends")
    var selectedMode by remember { mutableStateOf("Movies") }
    val trendingMovies by viewModel.trendingMovies
    val reviewedByFriends by viewModel.moviesReviewedByFriends
    val recommendations by viewModel.recommendedMovies

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let { userProfileViewModel.fetchUserProfile(it) }
    }

    val currentUser by userProfileViewModel.userProfile

    LaunchedEffect(currentUser) {
        currentUser?.let {
            viewModel.fetchReviewedByFriends(it.id)
            viewModel.fetchReviewedByFriends(it.id)
            viewModel.fetchRecommendedMovies(it.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (selectedMode == "Movies") "Movies" else "Reviews",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Show Reviews")
                Switch(
                    checked = selectedMode == "Reviews",
                    onCheckedChange = { isChecked ->
                        selectedMode = if (isChecked) "Reviews" else "Movies"
                        selectedFilter = if (isChecked) "Reviewed By Friends" else "Trending"
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (selectedMode == "Movies") {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                horizontalArrangement = Arrangement.Center
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
                "Trending" -> MoviesFeedScreen(navController, trendingMovies)
                else -> MoviesFeedScreen(navController, recommendations)
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                items(reviewedFilter) { filter ->
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
                "Reviewed By Friends" -> ReviewsFeedScreen(navController, reviewedByFriends)
            }
        }
    }
}

@Composable
fun MoviesFeedScreen(navController: NavController, movies: List<Movie>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movies) { movie ->
            MovieItem(movie, onClick = {
                navController.navigate(Screens.MovieDetail.createRoute(movie.id))
            })
        }
    }
}

@Composable
fun ReviewsFeedScreen(navController: NavController, reviews: List<MovieReview>) {
    if (reviews.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Reviews From Friends Yet",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(reviews) { review ->
            ReviewItem(review, onClick = {
                navController.navigate(Screens.MovieDetail.createRoute(review.movieId))
            })
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie, onClick: () -> Unit,
    viewModel: MyFeedViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var trailer by remember { mutableStateOf<String?>(null) }

    val likedMovies = viewModel.likedMovies.value
    val dislikedMovies = viewModel.dislikedMovies.value
    val savedMovies = viewModel.savedMovies.value

    val isLiked = likedMovies[movie.title] ?: false
    val isDisliked = dislikedMovies[movie.title] ?: false
    val isSaved = savedMovies[movie.title] ?: false

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let { userProfileViewModel.fetchUserProfile(it) }
    }

    val currentUser by userProfileViewModel.userProfile

    LaunchedEffect(currentUser) {
        currentUser?.let { viewModel.fetchMoviesState(currentUser!!.id) }
    }

    LaunchedEffect(Unit) {
        trailer = viewModel.getTrailer(movie.id)
    }

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
            Text(
                text = "Overview: ${if (movie.overview != "") movie.overview else "No Overview Available"}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Convert rating to stars (scale 10-point rating to 5-star system)
            val ratingOutOfFive = (movie.vote_average.toFloat() / 2).toInt().coerceIn(0, 5)
            val emptyStars = 5 - ratingOutOfFive

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Rating: ", style = MaterialTheme.typography.labelLarge)
                repeat(ratingOutOfFive) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Star Outline",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Filled Star",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                repeat(emptyStars) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Empty Star",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = {
            if (isLiked) {
                // Remove from liked
                viewModel.saveLikedMovie(currentUser?.id ?: "", movie.title, remove = true)
            } else {
                // Add to liked
                viewModel.saveLikedMovie(currentUser?.id ?: "", movie.title, remove = false)

                // If it was disliked, remove from disliked list
                if (isDisliked) {
                    viewModel.saveDislikedMovie(currentUser?.id ?: "", movie.title, remove = true)
                }
            }
        }) {
            Icon(
                Icons.Default.ThumbUp,
                contentDescription = "Like",
                tint = if (isLiked) Color.Blue else Color.Black
            )
        }

        IconButton(onClick = {
            if (isDisliked) {
                // Remove from disliked
                viewModel.saveDislikedMovie(currentUser?.id ?: "", movie.title, remove = true)
            } else {
                // Add to disliked
                viewModel.saveDislikedMovie(currentUser?.id ?: "", movie.title, remove = false)

                // If it was liked, remove from liked list
                if (isLiked) {
                    viewModel.saveLikedMovie(currentUser?.id ?: "", movie.title, remove = true)
                }
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_thumbs_down),
                contentDescription = "Dislike",
                tint = if (isDisliked) Color.Blue else Color.Black
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = {
            viewModel.saveMovie(currentUser?.id ?: "", movie.title)
        }) { Text(if (isSaved) "Saved" else "Save") }

        TextButton(
            onClick = {
                trailer?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                }
            }
        ) { Text("Watch Trailer") }
    }
}

@Composable
fun ReviewItem(
    review: MovieReview, onClick: () -> Unit,
    viewModel: MyFeedViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var trailer by remember { mutableStateOf<String?>(null) }

    val savedMovies = viewModel.savedMovies.value
    val isSaved = savedMovies[review.movieTitle] ?: false

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let { userProfileViewModel.fetchUserProfile(it) }
    }

    val currentUser by userProfileViewModel.userProfile

    LaunchedEffect(currentUser) {
        currentUser?.let { viewModel.fetchMoviesState(currentUser!!.id) }
    }

    LaunchedEffect(Unit) {
        trailer = viewModel.getTrailer(review.movieId)
    }

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
            Text(text = review.movieTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Reviewed by: ${review.reviewerName}", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Review: ${review.reviewText}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            val ratingOutOfFive = review.rating.coerceIn(0, 5)
            val emptyStars = 5 - ratingOutOfFive

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Rating: ", style = MaterialTheme.typography.labelLarge)
                repeat(ratingOutOfFive) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Star Outline",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Filled Star",
                            tint = Color.Yellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                repeat(emptyStars) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Empty Star",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(onClick = {
            viewModel.saveMovie(currentUser?.id ?: "", review.movieTitle)
        }) { Text(if (isSaved) "Saved Movie" else "Save Movie") }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = {
                trailer?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                }
            }
        ) { Text("Watch Trailer") }
    }
}
