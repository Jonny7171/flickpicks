package com.example.flickpicks.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SignUp(navController: NavController) {
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

    fun performSignUp() {
        // Validate all fields
        validateFields()

        if (
            firstNameError || lastNameError ||
            emailError || phoneError ||
            usernameError || passwordError
        ) {
            return
        }

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
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        // FIRST NAME
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
                text = "Please enter a last name",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Error text
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
                text = "Email is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Error text
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
                text = "Phone number is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Error text
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
                text = "Username is required",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Error text
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (it.isNotBlank()) passwordError = false
            },
            label = { Text("Password") },
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
            onClick = { performSignUp() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Complete Sign Up")
        }
    }
}
