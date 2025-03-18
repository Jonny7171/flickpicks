package com.example.flickpicks.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EditProfile(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    if (userId == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No user is logged in.")
        }
        return
    }

    val currentUser = userProfileViewModel.userProfile.value

    // Fetch the user profile
    LaunchedEffect(userId) {
        if (currentUser == null) {
            userProfileViewModel.fetchUserProfile(userId)
        }
    }

    // Local state for form fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Error states and messages
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var currentPasswordError by remember { mutableStateOf(false) }
    var newPasswordError by remember { mutableStateOf(false) }
    var firstNameErrorMessage by remember { mutableStateOf("") }
    var lastNameErrorMessage by remember { mutableStateOf("") }
    var emailErrorMessage by remember { mutableStateOf("") }
    var phoneErrorMessage by remember { mutableStateOf("") }
    var usernameErrorMessage by remember { mutableStateOf("") }
    var currentPasswordErrorMessage by remember { mutableStateOf("") }
    var newPasswordErrorMessage by remember { mutableStateOf("") }

    //prepopulate fields
    var hasPopulated by remember { mutableStateOf(false) }
    if (currentUser != null && !hasPopulated) {
        val nameParts = currentUser.name.split(" ")
        firstName = nameParts.getOrNull(0) ?: ""
        lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else ""
        email = currentUser.email
        phoneNumber = currentUser.phoneNumber
        username = currentUser.userName
        hasPopulated = true
    }

    fun validateFields(): Boolean {
        var valid = true
        firstNameError = false
        lastNameError = false
        emailError = false
        phoneError = false
        usernameError = false
        currentPasswordError = false
        newPasswordError = false

        if (firstName.isBlank()) {
            firstNameError = true
            firstNameErrorMessage = "First name is required."
            valid = false
        }
        if (lastName.isBlank()) {
            lastNameError = true
            lastNameErrorMessage = "Last name is required."
            valid = false
        }
        if (email.isBlank()) {
            emailError = true
            emailErrorMessage = "Email is required."
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = true
            emailErrorMessage = "Enter a valid email address."
            valid = false
        }
        if (phoneNumber.isBlank()) {
            phoneError = true
            phoneErrorMessage = "Phone number is required."
            valid = false
        } else {
            val phoneRegex = "^[0-9]{10}$".toRegex()
            if (!phoneNumber.matches(phoneRegex)) {
                phoneError = true
                phoneErrorMessage = "Invalid phone number. Must be 10 digits."
                valid = false
            }
        }
        if (username.isBlank()) {
            usernameError = true
            usernameErrorMessage = "Username is required."
            valid = false
        }

        // If user is updating password
        if (newPassword.isNotBlank()) {
            // Current password must not be blank
            if (currentPassword.isBlank()) {
                currentPasswordError = true
                currentPasswordErrorMessage = "Enter your current password to update."
                valid = false
            }
            // Check new password requirements
            val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{12,}\$".toRegex()
            if (!newPassword.matches(passwordRegex)) {
                newPasswordError = true
                newPasswordErrorMessage =
                    "Must be 12+ chars & include uppercase, lowercase, digit, special char."
                valid = false
            }
        }
        return valid
    }
    fun updateProfileFieldsAndGoBack() {
        // Combine first & last names
        val updatedName = "$firstName $lastName".trim()

        val updates = mapOf<String, Any>(
            "name" to updatedName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "userName" to username,
            "password" to newPassword
        )

        userProfileViewModel.updateUserProfile(userId, updates)

        navController.navigate(Screens.Profile.screen) {
            popUpTo(Screens.Profile.screen) { inclusive = true }
        }
    }

    fun performEdit() {
        if (!validateFields()) return

        // do password check
        val user = auth.currentUser ?: return

        // If newPassword is blank, skip password update
        if (newPassword.isBlank()) {
            // Just update Firestore with profile fields
            updateProfileFieldsAndGoBack()
        } else {
            // Reauth first
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // Reauth success, update password
                    user.updatePassword(newPassword).addOnCompleteListener { passTask ->
                        if (passTask.isSuccessful) {
                            Log.d("EditProfile", "Password updated successfully.")
                            // Now update Firestore
                            updateProfileFieldsAndGoBack()
                        } else {
                            newPasswordError = true
                            newPasswordErrorMessage = "Failed to update password."
                            Log.e("EditProfile", "Failed to update password: ${passTask.exception}")
                        }
                    }
                } else {
                    currentPasswordError = true
                    currentPasswordErrorMessage = "Current password is incorrect."
                    Log.e("EditProfile", "Reauth failed: ${reauthTask.exception}")
                }
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Picture
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val profilePicUrl = currentUser?.profilePicUrl ?: ""
            Image(
                painter = if (profilePicUrl.isNotBlank())
                    rememberAsyncImagePainter(profilePicUrl)
                else
                    rememberAsyncImagePainter("https://via.placeholder.com/150"),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // First Name
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
                if (it.isNotBlank()) firstNameError = false
            },
            label = { Text("First Name") },
            isError = firstNameError,
            modifier = Modifier.fillMaxWidth()
        )
        if (firstNameError) {
            Text(
                text = firstNameErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Last Name
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
                if (it.isNotBlank()) lastNameError = false
            },
            label = { Text("Last Name") },
            isError = lastNameError,
            modifier = Modifier.fillMaxWidth()
        )
        if (lastNameError) {
            Text(
                text = lastNameErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (it.isNotBlank()) emailError = false
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError) {
            Text(
                text = emailErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Phone Number
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                if (it.isNotBlank()) phoneError = false
            },
            label = { Text("Phone Number") },
            isError = phoneError,
            modifier = Modifier.fillMaxWidth()
        )
        if (phoneError) {
            Text(
                text = phoneErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (it.isNotBlank()) usernameError = false
            },
            label = { Text("Username") },
            isError = usernameError,
            modifier = Modifier.fillMaxWidth()
        )
        if (usernameError) {
            Text(
                text = usernameErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Update Password", fontSize = 18.sp)

        // Current Password
        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            isError = currentPasswordError,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        if (currentPasswordError) {
            Text(
                text = currentPasswordErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // New Password
        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                if (it.isNotBlank()) newPasswordError = false
            },
            label = { Text("New Password") },
            isError = newPasswordError,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.Lock else Icons.Filled.Lock,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )
        if (newPasswordError) {
            Text(
                text = newPasswordErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { performEdit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
