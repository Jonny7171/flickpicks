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
import androidx.navigation.NavController

@Composable
fun Settings (navController: NavController) {
    var isDarkMode by remember {mutableStateOf(false)}
    var notificationsEnabled by remember {mutableStateOf(true)}
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showLogOutDialog by remember { mutableStateOf(false) }
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
        SettingsItem(Icons.Filled.Warning, "View Blocked Users") {/* add navigation*/ }

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

        SettingsToggleItem("Enable Notifications", notificationsEnabled) { notificationsEnabled = it }
        SettingsToggleItem("Dark Mode", isDarkMode) { isDarkMode = it }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // History Management
        Text(
            text = "History",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        SettingsItem(Icons.Filled.ShoppingCart, "View Saved Movies") { /* add navigation*/}
        SettingsItem(Icons.Filled.Delete, "Clear Search History") { showClearHistoryDialog = true }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Log Out
        SettingsItem(Icons.AutoMirrored.Filled.ExitToApp, "Log Out", Color.Red) { showLogOutDialog = true }

        // Dialogs for Confirmation
        if (showClearHistoryDialog) {
            ConfirmationDialog(
                title = "Clear History",
                message = "Are you sure you want to clear your search history?",
                onConfirm = { /* Handle clear history */ showClearHistoryDialog = false },
                onDismiss = { showClearHistoryDialog = false }
            )
        }

        if (showLogOutDialog) {
            ConfirmationDialog(
                title = "Log Out",
                message = "Are you sure you want to log out?",
                onConfirm = { /* Handle logout */ navController.navigate(Screens.Entry.screen)},
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


