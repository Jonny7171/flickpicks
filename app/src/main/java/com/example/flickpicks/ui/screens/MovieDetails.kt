package com.example.flickpicks.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.ui.viewmodels.MyFeedViewModel
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MovieDetailScreen(
    movieId: String,
    navController: NavController,
    goToAddReviewTab: Boolean = false,
    viewModel: MyFeedViewModel = hiltViewModel()
) {

    val selectedMovie by viewModel.selectedMovie
    val watchProviders by viewModel.watchProviders
    val context = LocalContext.current
    var trailer by remember { mutableStateOf<String?>(null) }
    var movieReviews by remember { mutableStateOf<List<Pair<String, String>>?>(null) }

    LaunchedEffect(movieId) {
        trailer = viewModel.getTrailer(movieId)
        movieReviews = viewModel.getMovieReviews(movieId)
        viewModel.getMovieDetails(movieId)
        viewModel.fetchWatchProviders(movieId)
    }

    var selectedTab by remember { mutableStateOf("Overview") }
    val tabs = listOf("Overview", "Reviews", "Add Review")

    LaunchedEffect(goToAddReviewTab) {
        if (goToAddReviewTab) {
            selectedTab = "Add Review"
        }
    }

    if (selectedMovie == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${selectedMovie!!.poster_path}"),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(250.dp).fillMaxWidth().background(Color.White)
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
                    Button(onClick = {
                        trailer?.let {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                            context.startActivity(intent)
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
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
            "Overview" -> OverviewTab(selectedMovie!!, watchProviders)
            "Reviews" -> ReviewsTab(movieReviews, movieId)
            "Add Review" -> AddReviewTab(movieId)
        }
    }
}

@Composable
fun OverviewTab(
    movie: Movie,
    watchProviders: List<String>,
    viewModel: MyFeedViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val savedMovies = viewModel.savedMovies.value
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

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Tagline: ${movie.tagline}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Overview: ${if (movie.overview != "") movie.overview else "No Overview Available"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Genres: ${movie.genres.joinToString(", ")}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (watchProviders.isNotEmpty())
                "Available on: ${watchProviders.joinToString(", ")}"
            else
                "Available on: Unknown",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.saveMovie(currentUser?.id ?: "", movie.title)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isSaved) "Saved to Wishlist" else "Save to Wishlist")
        }
    }
}

@Composable
fun ReviewsTab(
    reviews: List<Pair<String, String>>?,
    movieId: String,
    viewModel: MyFeedViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    var friendReviews by remember { mutableStateOf<List<MovieReview>>(emptyList()) }

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let { userProfileViewModel.fetchUserProfile(it) }
    }

    val currentUser by userProfileViewModel.userProfile

    LaunchedEffect(userId) {
        userId?.let {
            val fetchedReviews = viewModel.getFriendsMovieReviews(it, movieId)
            friendReviews = fetchedReviews
        }
    }

    if (reviews.isNullOrEmpty() && friendReviews.isEmpty()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "No Reviews Available Yet",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(friendReviews) { review ->
            Card(modifier = Modifier.padding(8.dp).fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "👤 ${review.reviewerName}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = review.reviewText, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        items(reviews ?: emptyList()) { review ->
            Card(modifier = Modifier.padding(8.dp).fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "👤 ${review.first}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = review.second, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun AddReviewTab(
    movieId: String,
    viewModel: MyFeedViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var streamingPlatform by remember { mutableStateOf("") }
    var postReviewText by remember { mutableStateOf("Post Review") }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var commentError by remember { mutableStateOf("") }
    var platformError by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(true) }

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let { userProfileViewModel.fetchUserProfile(it) }
    }

    val currentUser by userProfileViewModel.userProfile

    var existingReview: MovieReview? by remember { mutableStateOf(null) }

    LaunchedEffect(movieId, currentUser) {
        currentUser?.let { profile ->
            existingReview = viewModel.getCurrUserMovieReview(profile.id, movieId)
            existingReview?.let {
                rating = it.rating
                comment = it.reviewText
                streamingPlatform = it.streamingPlatform
                postReviewText = "Review Posted"
                isButtonEnabled = false
                isEditMode = false
            }
        }
    }

    fun checkErrors(): Boolean {
        var valid = true
        commentError = ""
        platformError = ""

        if (comment.isEmpty()) {
            commentError = "Please enter a comment about the movie."
            valid = false
        }

        if (streamingPlatform.isEmpty()) {
            platformError = "Please enter where you watched the movie."
            valid = false
        }

        return valid
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Your Rating:")
            if (existingReview != null || postReviewText == "Review Posted") {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        isEditMode = !isEditMode
                        if (isEditMode) {
                            postReviewText = "Post Review"
                            isButtonEnabled = true
                        } else {
                            postReviewText = "Review Posted"
                            isButtonEnabled = false
                        }
                    }) {
                        Text(text = if (isEditMode) "Cancel" else "Edit")
                    }
                }
            }
        }
        Row {
            for (i in 1..5) {
                IconButton(
                    onClick = { rating = if (rating == i) i - 1 else i },
                    enabled = isEditMode
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "$i Stars",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                        if (i <= rating) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "$i Stars",
                                tint = Color.Yellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Your Review:")
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            modifier = Modifier.fillMaxWidth(),
            isError = commentError.isNotEmpty(),
            enabled = isEditMode,
            trailingIcon = {
                if (commentError.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red
                    )
                }
            }
        )
        if (commentError.isNotEmpty()) {
            Text(text = commentError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Where did you watch the movie?")
        OutlinedTextField(
            value = streamingPlatform,
            onValueChange = { streamingPlatform = it },
            modifier = Modifier.fillMaxWidth(),
            isError = platformError.isNotEmpty(),
            enabled = isEditMode,
            trailingIcon = {
                if (platformError.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red
                    )
                }
            }
        )
        if (platformError.isNotEmpty()) {
            Text(text = platformError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (checkErrors()) {
                    currentUser?.let {
                        viewModel.postReview(it.id, movieId, rating.toString(), comment, streamingPlatform)
                    }
                    postReviewText = "Review Posted"
                    isButtonEnabled = false
                    isEditMode = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled
        ) {
            Text(text = postReviewText)
        }
    }
}
