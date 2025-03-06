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
    // Get MainViewModel instance
    val mainViewModel = viewModel<MainViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )

    //List of genres
    val commonGenres = listOf("Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi", "Thriller")

    // Track which genres are selected
    val selectedGenres = remember { mutableStateListOf<String>() }

    // For displaying an error if the user tries to proceed with fewer than 2 genres
    var errorMessage by remember { mutableStateOf("") }

    fun finalizePreferences() {
        // Require at least 2 genres
        if (selectedGenres.size < 2) {
            errorMessage = "Please select at least 2 genres."
            return
        }
        // Add them to the user in the db
        mainViewModel.addPreferencesToCurrentUser(selectedGenres)

        // Navigate to MyFeed after saving preferences
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

        // List all common genres with a checkbox
        LazyColumn {
            items(commonGenres) { genre ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedGenres.contains(genre)) {
                                selectedGenres.remove(genre)
                            } else {
                                selectedGenres.add(genre)
                            }
                        }
                        .padding(8.dp),
                    // Keep checkboxes vertically centered
                    horizontalArrangement = Arrangement.Start
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

        Spacer(modifier = Modifier.height(24.dp))

        //Error
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Button to confirm preferences
        Button(
            onClick = { finalizePreferences() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Complete Preferences")
        }
    }
}
