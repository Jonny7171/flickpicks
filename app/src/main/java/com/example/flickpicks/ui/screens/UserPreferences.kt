package com.example.flickpicks.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserPreferences(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {

    val commonGenres = listOf(
        "Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary",
        "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Mystery",
        "Romance", "Science Fiction", "TV Movie", "Thriller", "War", "Western"
    )

    // Track which genres are selected
    val selectedGenres = remember { mutableStateListOf<String>() }

    var errorMessage by remember { mutableStateOf("") }

    fun finalizePreferences() {
        // Require at least 2 genres
        if (selectedGenres.size < 2) {
            errorMessage = "Please select at least 2 genres."
            return
        }

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            errorMessage = "You must be logged in to select preferences"
            return
        }
        val userProfile = userProfileViewModel.getUserProfile(userId)
        Log.d("Firestore", "$userProfile")
        userProfile.let {
            userProfileViewModel.updateUserProfile(userId, mapOf("genrePreferences" to selectedGenres))
            navController.navigate(Screens.MyFeed.screen) {
                popUpTo(Screens.Entry.screen) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Title + scrollable list of genres
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Select Your Interests",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Chunk the genres list into rows of 2 items
            val chunkedGenres = commonGenres.chunked(2)
            LazyColumn {
                items(chunkedGenres) { genreRow ->
                    Row {
                        for (genre in genreRow) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (selectedGenres.contains(genre)) {
                                            selectedGenres.remove(genre)
                                        } else {
                                            selectedGenres.add(genre)
                                        }
                                    }
                                    .padding(8.dp)
                            ) {
                                val isChecked = selectedGenres.contains(genre)
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            selectedGenres.add(genre)
                                        } else {
                                            selectedGenres.remove(genre)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = genre)
                            }
                        }
                    }
                }
            }
        }

        // Error message + button
        Column {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { finalizePreferences() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Complete Preferences")
            }
        }
    }
}