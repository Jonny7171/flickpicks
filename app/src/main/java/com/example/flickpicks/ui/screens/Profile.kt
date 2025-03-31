package com.example.flickpicks.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flickpicks.R
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

private val avatarOptions = listOf("dog", "cat", "glasses", "miami")
private val avatarMap = mapOf(
    "dog" to R.drawable.dog,
    "cat" to R.drawable.cat,
    "glasses" to R.drawable.glassses,
    "miami" to R.drawable.miami
)

@Composable
fun Profile(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    LaunchedEffect(userId) {
        userId?.let { userProfileViewModel.fetchUserProfile(it) }
    }
    val currentUser by userProfileViewModel.userProfile
    val user = currentUser
    if (userId == null || user == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No user data found. Please sign up.")
        }
        return
    }
    var editSavedMovies by remember { mutableStateOf(false) }
    var editLikedMovies by remember { mutableStateOf(false) }
    var editDislikedMovies by remember { mutableStateOf(false) }
    var editReviews by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Profile",
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = { navController.navigate(Screens.Settings.screen) }) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            val picKey = currentUser?.profilePicUrl ?: ""
            val avatarRes = if (picKey.isBlank()) 0 else avatarMap[picKey] ?: 0
            if (avatarRes != 0) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = user.name.ifBlank { "Add your name" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate(Screens.EditProfile.screen) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Edit Profile", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                "Email: ${user.email.ifBlank { "Not provided" }}",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Phone: ${user.phoneNumber.ifBlank { "Not provided" }}",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Username: ${user.userName.ifBlank { "Not provided" }}",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Movie Preferences",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = { navController.navigate(Screens.UserPreferences.screen) }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                }
                if (user.genrePreferences.isNullOrEmpty()) {
                    Text("No preferences selected", color = Color.Gray)
                } else {
                    PreferencesList(user.genrePreferences)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Saved Movies",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(onClick = { editSavedMovies = !editSavedMovies }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }
            if (user.moviesSaved.isNullOrEmpty()) {
                Text("No saved movies", color = Color.Gray)
            } else {
                EditableMovieList(
                    movies = user.moviesSaved,
                    editMode = editSavedMovies,
                    onRemove = { movie ->
                        userProfileViewModel.removeSavedMovie(movie)
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Liked Movies",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(onClick = { editLikedMovies = !editLikedMovies }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }
            if (user.moviesLiked.isNullOrEmpty()) {
                Text("No liked movies", color = Color.Gray)
            } else {
                EditableMovieList(
                    movies = user.moviesLiked,
                    editMode = editLikedMovies,
                    onRemove = { movie ->
                        userProfileViewModel.removeLikedMovie(movie)
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Disliked Movies",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(onClick = { editDislikedMovies = !editDislikedMovies }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }
            if (user.moviesDisliked.isNullOrEmpty()) {
                Text("No disliked movies", color = Color.Gray)
            } else {
                EditableMovieList(
                    movies = user.moviesDisliked,
                    editMode = editDislikedMovies,
                    onRemove = { movie ->
                        userProfileViewModel.removeDislikedMovie(movie)
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Ratings & Reviews",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(onClick = { editReviews = !editReviews }) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
            }
            if (user.moviesReviewed.isNullOrEmpty()) {
                Text("No reviews available", color = Color.Gray)
            } else {
                EditableRatingsList(
                    reviews = user.moviesReviewed,
                    editMode = editReviews,
                    onRemove = { review ->
                        userProfileViewModel.removeReview(review)
                    },
                    onEdit = { movieId ->
                        navController.navigate(Screens.MovieDetail.createRoute(movieId))
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun PreferencesList(preferences: List<String>) {
    val displayed = if (preferences.size > 5) preferences.take(5) else preferences
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        displayed.forEach {
            Text("• $it", fontSize = 14.sp, color = Color.Black)
        }
        if (preferences.size > 5) {
            Text("...")
        }
    }
}

@Composable
fun EditableMovieList(
    movies: List<String>,
    editMode: Boolean,
    onRemove: (String) -> Unit
) {
    val displayedMovies = if (!editMode && movies.size > 5) {
        movies.take(5)
    } else {
        movies
    }
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        displayedMovies.forEach { movie ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🎬 $movie",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                if (editMode) {
                    IconButton(onClick = { onRemove(movie) }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
        if (!editMode && movies.size > 5) {
            Text("...")
        }
    }
}

@Composable
fun EditableRatingsList(
    reviews: List<MovieReview>,
    editMode: Boolean,
    onRemove: (MovieReview) -> Unit,
    onEdit: (String) -> Unit
) {
    val displayedReviews = if (!editMode && reviews.size > 5) {
        reviews.take(5)
    } else {
        reviews
    }
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        displayedReviews.forEach { review ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    review.movieTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                if (editMode) {
                    IconButton(onClick = { onRemove(review) }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                    IconButton(onClick = { onEdit(review.movieId) }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                }
            }
        }
        if (!editMode && reviews.size > 5) {
            Text("...")
        }
    }
}