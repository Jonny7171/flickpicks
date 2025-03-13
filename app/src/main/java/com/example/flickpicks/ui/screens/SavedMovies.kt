package com.example.flickpicks.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SavedMovies(navController: NavController) {
    val userProfileViewModel = viewModel<UserProfileViewModel>(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    // Fetch user profile when screen is loaded
    LaunchedEffect(userId) {
        userId?.let { userProfileViewModel.fetchUserProfile(it) }
    }

    val currentUser by userProfileViewModel.userProfile

    if (userId == null || currentUser == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("There was an error, please try again later")
        }
        return
    }

    val savedMovies = currentUser?.moviesSaved ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Saved Movies",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (savedMovies.isEmpty()) {
            Text(
                text = "No saved movies found.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)  ,
                color = Color.Gray
            )
        } else {
            SavedMoviesList(currentUser !!.moviesSaved)
        }
    }
}

@Composable
fun SavedMoviesList(savedMovies: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        savedMovies.forEach {
            Text(text = "• $it", fontSize = 14.sp, color = Color.Black)
        }
    }
}