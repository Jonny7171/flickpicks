package com.example.flickpicks.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flickpicks.ui.viewmodels.MainViewModel

@Composable
fun UserPreferences(
    navController: NavController
) {
    val mainViewModel = viewModel<MainViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )

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
        // Save preferences to your backend
        mainViewModel.addPreferencesToCurrentUser(selectedGenres)

        // Navigate to the feed after saving
        navController.navigate(Screens.MyFeed.screen) {
            popUpTo(Screens.Entry.screen) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Your Interests",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- UPDATED SECTION: 2-column layout via chunked list ---
        val chunkedGenres = commonGenres.chunked(2)
        LazyColumn {
            items(chunkedGenres) { genreRow ->
                Row {
                    for (genre in genreRow) {
                        Row(
                            modifier = Modifier
                                .weight(1f) // each genre takes equal width in the row
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

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = { finalizePreferences() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Complete Preferences")
        }
    }
}
