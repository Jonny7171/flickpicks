package com.example.flickpicks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BlockedUsers(
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

    val blockedUsers = currentUser?.blockedUsers ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Blocked Users",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (blockedUsers.isEmpty()) {
            Text(
                text = "No blocked users found.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)  ,
                color = Color.Gray
            )
        } else {
            BlockedUsersList(currentUser !!.blockedUsers)
        }
    }
}

@Composable
fun BlockedUsersList(blockedUsers: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        blockedUsers.forEach {
            Text(text = "• $it", fontSize = 14.sp, color = Color.Black)
        }
    }
}