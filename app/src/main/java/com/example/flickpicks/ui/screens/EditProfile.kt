package com.example.flickpicks.ui.screens
import androidx.compose.foundation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.*

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter


import androidx.compose.ui.Alignment



@Composable
fun EditProfile(navController: NavController) {
    // Field values
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Error states for each field
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    fun validateFields() {
        // Reset all errors first
        firstNameError = false
        lastNameError = false
        emailError = false
        phoneError = false
        usernameError = false
        passwordError = false

        // Mark fields as error if they're empty
        if (firstName.isBlank()) firstNameError = true
        if (lastName.isBlank()) lastNameError = true
        if (email.isBlank()) emailError = true
        if (phoneNumber.isBlank()) phoneError = true
        if (username.isBlank()) usernameError = true
        if (password.isBlank()) passwordError = true
    }

    fun performEdit() {
        // Validate all fields
        validateFields()

        if (
            firstNameError || lastNameError ||
            emailError || phoneError ||
            usernameError || passwordError
        ) {
            return
        }

        navController.navigate(Screens.Profile.screen) {
            popUpTo(Screens.Profile.screen) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(scrollState),

    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon( imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Picture
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,

            ) {
            Image(
                painter = rememberAsyncImagePainter("https://www.example.com/profile.jpg"),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),


                )

        }


        Spacer(modifier = Modifier.height(8.dp))

        // User Name


        Spacer(modifier = Modifier.height(8.dp))
        // FIRST NAME
        OutlinedTextField(

            value = "Jane",
            onValueChange = {
                firstName = it
                if (it.isNotBlank()) firstNameError = false
            },
            label = { Text("First Name") },

            isError = firstNameError,
            modifier = Modifier.fillMaxWidth()
        )

        // Error text
        if (firstNameError) {
            Text(
                text = "Please enter a first name",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Error text
        OutlinedTextField(
            value = "Smith",
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
                text = "Please enter a last name",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Error text
        OutlinedTextField(
            value = "janesmith123@gmail.com",
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
                text = "Email is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Error text
        OutlinedTextField(
            value = "1234567890",
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
                text = "Phone number is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Error text
        OutlinedTextField(
            value = "janesmith",
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
                text = "Username is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text="Update Password")
        OutlinedTextField(
            value = "",
            onValueChange = {
                password = it
                if (it.isNotBlank()) passwordError = false
            },
            label = { Text("Current Password") },
            isError = passwordError,
            modifier = Modifier.fillMaxWidth()
        )

        // Error text
        OutlinedTextField(
            value = "",
            onValueChange = {
                password = it
                if (it.isNotBlank()) passwordError = false
            },
            label = { Text("New Password") },
            isError = passwordError,
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError) {
            Text(
                text = "Password is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Complete Sign Up
        Button(
            onClick = { performEdit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
