package com.example.flickpicks.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val currentUserProfile = userProfileViewModel.userProfile.value

    if (userId == null) {
        Text("You must be logged in to set preferences.")
        return
    }

    LaunchedEffect(userId) {
        if (currentUserProfile == null) {
            userProfileViewModel.fetchUserProfile(userId)
        }
    }

    val commonGenres = listOf(
        "Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary",
        "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Mystery",
        "Romance", "Science Fiction", "TV Movie", "Thriller", "War", "Western"
    )

    val selectedGenres = remember { mutableStateListOf<String>() }
    var hasPopulated by remember { mutableStateOf(false) }

    if (currentUserProfile != null && !hasPopulated) {
        selectedGenres.clear()
        selectedGenres.addAll(currentUserProfile.genrePreferences)
        hasPopulated = true
    }

    var errorMessage by remember { mutableStateOf("") }

    fun finalizePreferences() {
        if (selectedGenres.size < 2) {
            errorMessage = "Please select at least 2 genres."
            return
        }

        userProfileViewModel.updateUserProfile(userId, mapOf("genrePreferences" to selectedGenres))

        navController.navigate(Screens.MyFeed.screen) {
            popUpTo(Screens.Entry.screen) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Select Your Interests",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

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
