package com.example.flickpicks.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flickpicks.R
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.ui.theme.BlueNew
import com.example.flickpicks.ui.viewmodels.SearchViewModel
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Search(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    var searchText by remember { mutableStateOf("") }
    val movieResults by viewModel.searchResults
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var searchJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(searchText) {
        searchJob?.cancel() // Cancel previous job if exists
        searchJob = coroutineScope.launch {
            delay(300) // Debounce: Wait 300ms before making API call
            if (searchText.isNotEmpty()) {
                viewModel.searchMovies(searchText)
            }
        }
    }

    fun performSearch() {
        keyboardController?.hide()
        focusManager.clearFocus()
        viewModel.searchMovies(searchText)

    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search Movies", fontSize = 18.sp, color = BlueNew) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            singleLine = true
        )

        Button(
            onClick = { performSearch() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }
        if (movieResults.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            SearchResultsScreen(navController, movieResults)
        }
    }
}

@Composable
fun SearchResultsScreen(navController: NavController, movies: List<Movie>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movies) { movie ->
            SearchMovieItem(movie, onClick = {
                navController.navigate(Screens.MovieDetail.createRoute(movie.id))
            })
        }
    }
}


@Composable
fun SearchMovieItem(
    movie: Movie, onClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
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

    LaunchedEffect(Unit) {
        trailer = viewModel.getTrailer(movie.id)
        userId?.let { userProfileViewModel.fetchUserProfile(it) }

    }
    val currentUser by userProfileViewModel.userProfile

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
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
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
    }
}
