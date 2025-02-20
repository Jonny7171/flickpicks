package com.example.flickpicks.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.ui.screens.Screens
import com.example.flickpicks.ui.viewmodels.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

@Composable
fun Profile(navController: NavController) {
    val mainViewModel = viewModel<MainViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
    val currentUser = mainViewModel.getCurrentUser() // Assumes getCurrentUser() is implemented in MainViewModel

    // Helper function to display a field or a default message if empty
    fun displayField(field: String): String =
        if (field.isBlank()) "Add information to have it show here" else field

    // If no user data is found, show a placeholder message
    if (currentUser == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No user data found. Please sign up.")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        // Profile Picture
        val profilePicUrl = currentUser.profilePicUrl ?: ""
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
            text = displayField(currentUser.name),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate(Screens.EditProfile.screen) },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Edit Profile", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Additional User Information
        Text(
            text = "Email: ${displayField(currentUser.email)}",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Phone: ${displayField(currentUser.phoneNumber)}",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Username: ${displayField(currentUser.userName)}",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
        if (currentUser.moviesPreferences.isEmpty()) {
            Text("Add information to have it show here", color = Color.Gray)
        } else {
            PreferencesList(currentUser.moviesPreferences)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Watch History Section
        Text(
            text = "Watch History",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        if (currentUser.moviesWatched.isEmpty()) {
            Text("Add information to have it show here", color = Color.Gray)
        } else {
            MovieList(currentUser.moviesWatched)
        }

        Spacer(modifier = Modifier.height(16.dp))

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
        if (currentUser.moviesReviewed.isEmpty()) {
            Text("Add information to have it show here", color = Color.Gray)
        } else {
            RatingsList(listOf("No ratings available"))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wish List Section (using moviesLiked as a stand-in)
        Text(
            text = "Wish List",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        if (currentUser.moviesLiked.isEmpty()) {
            Text("Add information to have it show here", color = Color.Gray)
        } else {
            MovieList(currentUser.moviesLiked)
        }
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
