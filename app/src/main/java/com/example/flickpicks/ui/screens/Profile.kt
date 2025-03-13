
package com.example.flickpicks.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Profile(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    // Fetch user profile when screen is loaded
    LaunchedEffect(userId) {
        userId?.let { userProfileViewModel.fetchUserProfile(it) }
    }

    val currentUser by userProfileViewModel.userProfile

    // If user is not logged in
    if (userId == null || currentUser == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No user data found. Please sign up.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Top Bar with Settings Icon
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
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Profile Picture
            val profilePicUrl = currentUser?.profilePicUrl ?: ""
            Image(
                painter = if (profilePicUrl.isNotBlank())
                    rememberAsyncImagePainter(profilePicUrl)
                else
                    rememberAsyncImagePainter("https://via.placeholder.com/150"), // Placeholder image
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display the User's Name
            Text(
                text = currentUser?.name ?: "Add your name",
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
                    Text(text = "Edit Profile", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Additional User Information
            Text(
                text = "Email: ${currentUser?.email ?: "Not provided"}",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Phone: ${currentUser?.phoneNumber ?: "Not provided"}",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Username: ${currentUser?.userName ?: "Not provided"}",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Movie Preferences Section
            Text(
                text = "Movie Preferences",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            if (currentUser?.genrePreferences.isNullOrEmpty()) {
                Text("No preferences selected", color = Color.Gray)
            } else {
                PreferencesList(currentUser!!.genrePreferences)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Watch History Section
            Text(
                text = "Saved Movies",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            if (currentUser?.moviesSaved.isNullOrEmpty()) {
                Text("No saved movies", color = Color.Gray)
            } else {
                MovieList(currentUser!!.moviesSaved)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            // Ratings & Reviews Section
            Text(
                text = "Ratings & Reviews",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            if (currentUser?.moviesReviewed.isNullOrEmpty()) {
                Text("No reviews available", color = Color.Gray)
            } else {
                RatingsList(listOf("No ratings available"))
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun PreferencesList(preferences: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        preferences.forEach {
            Text(text = "• $it", fontSize = 14.sp, color = Color.Black)
        }
    }
}

@Composable
fun MovieList(movies: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        movies.forEach {
            Text(text = "🎬 $it", fontSize = 14.sp, color = Color.Black)
        }
    }
}

@Composable
fun RatingsList(ratings: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        ratings.forEach {
            Text(text = "⭐ $it", fontSize = 14.sp, color = Color.Black)
        }
    }
}
