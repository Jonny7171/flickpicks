package com.example.flickpicks.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.example.flickpicks.ui.viewmodels.SettingsViewModel
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel

import com.google.firebase.auth.FirebaseAuth


@Composable
fun Settings
            (navController: NavController,
             userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    var isDarkMode by remember {mutableStateOf(false)}
    var showLogOutDialog by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val settingsViewModel: SettingsViewModel = hiltViewModel()

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
            Text("No settings found. Please sign up.")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Settings", fontSize = 22.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        // Account Settings
        Text(
            text = "Privacy & Security",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        SettingsItem(Icons.Filled.AccountCircle, "Profile Visibility") {/* add navigation*/ }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // App Preferences
        Text(
            text = "App Preferences",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        SettingsToggleItem("Dark Mode", isDarkMode) { isDarkMode = it }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Log Out
        SettingsItem(Icons.AutoMirrored.Filled.ExitToApp, "Log Out", Color.Red) { showLogOutDialog = true }

        if (showLogOutDialog) {
            ConfirmationDialog(
                title = "Log Out",
                message = "Are you sure you want to log out?",

                /*
                onConfirm = {
                    Firebase.auth.signOut()

                    navController.navigate(Screens.Entry.screen)
                            },

                 */
                onConfirm = {
                    settingsViewModel.logout {
                        navController.navigate(Screens.Entry.screen) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onDismiss = { showLogOutDialog = false }



            )
        }
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, color: Color = Color.Black, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, color = color)
    }
}

@Composable
fun SettingsToggleItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 16.sp, color = Color.Black)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}


@Composable
fun ConfirmationDialog(title: String, message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = { onConfirm() }) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text("Cancel") }
        }
    )
}



