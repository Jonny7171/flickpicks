package com.example.flickpicks.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.data.model.UserProfile
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.text.font.FontWeight



val mockUser = UserProfile(6, "Jane Doe", "...")

@Composable
fun Profile(navController: NavController) {
    fun performEdit() {
        navController.navigate(Screens.EditProfile.screen)
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
            Text(text = "My Profile", style=MaterialTheme.typography.headlineMedium)

            IconButton(onClick = { navController.navigate(Screens.Settings.screen)  }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Picture
        Image(
            painter = rememberAsyncImagePainter("...."),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User Name
        Text(
            text = mockUser.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { performEdit() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Edit Profile", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = "54", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Followers", fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(32.dp)) // Space between followers and following
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text(text = "48", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Following", fontSize = 14.sp, color = Color.Gray)
            }
        }
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
        PreferencesList(listOf("Sci-Fi", "Thriller", "Comedy", "Action"))

        Spacer(modifier = Modifier.height(16.dp))

        // Watch History
        Text(
            text = "Watch History",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        MovieList(listOf("Inception", "Interstellar", "Avatar"))

        Spacer(modifier = Modifier.height(16.dp))

        // Ratings & Reviews
        Text(
            text = "Ratings & Reviews",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        RatingsList(listOf("Inception - 5⭐", "The Matrix - 4.5⭐", "Avatar - 4⭐"))

        Spacer(modifier = Modifier.height(16.dp))

        // Wish List
        Text(
            text = "Wish List",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        MovieList(listOf("Dune: Part Two", "Tenet", "Blade Runner 2049", "Oppenheimer"))



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

