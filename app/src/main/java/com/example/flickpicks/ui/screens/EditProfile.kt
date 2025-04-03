package com.example.flickpicks.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flickpicks.R
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

private val avatarOptions = listOf("dog", "cat", "glasses", "miami")
private val avatarMap = mapOf(
    "dog" to R.drawable.dog,
    "cat" to R.drawable.cat,
    "glasses" to R.drawable.glassses,
    "miami" to R.drawable.miami
)

@Composable
fun EditProfile(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
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
    LaunchedEffect(userId) {
        if (currentUser == null) {
            userProfileViewModel.fetchUserProfile(userId)
        }
    }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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

    var chosenAvatar by remember { mutableStateOf(currentUser?.profilePicUrl ?: "dog") }

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
        if (newPassword.isNotBlank()) {
            if (currentPassword.isBlank()) {
                currentPasswordError = true
                currentPasswordErrorMessage = "Enter your current password to update."
                valid = false
            }
            val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{12,}\$".toRegex()
            if (!newPassword.matches(passwordRegex)) {
                newPasswordError = true
                newPasswordErrorMessage = "Must be 12+ chars & include uppercase, lowercase, digit, special char."
                valid = false
            }
        }
        return valid
    }

    fun updateProfileFieldsAndGoBack() {
        val updatedName = "$firstName $lastName".trim()
        val updates = mapOf<String, Any>(
            "name" to updatedName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "userName" to username,
            "password" to newPassword,
            "profilePicUrl" to chosenAvatar
        )
        userProfileViewModel.updateUserProfile(userId, updates)
        navController.navigate(Screens.Profile.screen) {
            popUpTo(Screens.Profile.screen) { inclusive = true }
        }
    }

    fun performEdit() {
        if (!validateFields()) return
        val userAuth = auth.currentUser ?: return
        if (newPassword.isBlank()) {
            updateProfileFieldsAndGoBack()
        } else {
            val credential = EmailAuthProvider.getCredential(userAuth.email!!, currentPassword)
            userAuth.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    userAuth.updatePassword(newPassword).addOnCompleteListener { passTask ->
                        if (passTask.isSuccessful) {
                            Log.d("EditProfile", "Password updated successfully.")
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (avatarMap.containsKey(chosenAvatar)) {
                Image(
                    painter = painterResource(avatarMap[chosenAvatar] ?: R.drawable.dog),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.dog),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            avatarOptions.forEach { key ->
                val resId = avatarMap[key] ?: R.drawable.dog
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = key,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .clickable { chosenAvatar = key }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            Text(firstNameErrorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

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
            Text(lastNameErrorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

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
            Text(emailErrorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

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
            Text(phoneErrorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

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
            Text(usernameErrorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Update Password", fontSize = 18.sp)

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            isError = currentPasswordError,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        if (currentPasswordError) {
            Text(currentPasswordErrorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

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
            Text(newPasswordErrorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
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