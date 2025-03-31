package com.example.flickpicks.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun SignUp(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    // Field values
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false)}

    // Error states for each field
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }


    // Error messages for each field
    var firstNameErrorMessage by remember { mutableStateOf("") }
    var lastNameErrorMessage by remember { mutableStateOf("") }
    var emailErrorMessage  by remember { mutableStateOf("") }
    var phoneErrorMessage by remember { mutableStateOf("") }
    var usernameErrorMessage by remember { mutableStateOf("") }
    var passwordErrorMessage by remember { mutableStateOf("") }

    var auth = FirebaseAuth.getInstance()
    var firebaseErrorMessage by remember { mutableStateOf("") }


    // Mark fields as error if they're empty
    fun validateFields() {
        firstNameError = firstName.isBlank()
        lastNameError = lastName.isBlank()
        emailError = email.isBlank()
        phoneError = phoneNumber.isBlank()
        usernameError = username.isBlank()
        passwordError = password.isBlank()

    }

    fun validatePhoneNumber(): Boolean {
        if (phoneNumber.isBlank()) {
            phoneError = true
            phoneErrorMessage = "Phone number is required."
            return false
        }

        val phoneRegex = "^[0-9]{10}$".toRegex()
        if (!phoneNumber.matches(phoneRegex)) {
            phoneError = true
            phoneErrorMessage = "Invalid phone number. Must be 10 digits."
            return false
        }
        return true
    }

    fun validateFirstName(): Boolean {
        if (firstName.isBlank()) {
            firstNameError = true
            firstNameErrorMessage = "First name is required."
            return false
        }
        return true
    }

    fun validateLastName(): Boolean {
        if (lastName.isBlank()) {
            lastNameError = true
            lastNameErrorMessage = "Last name is required."
            return false
        }
        return true
    }

    fun validateEmail(): Boolean {
        if (email.isBlank()) {
            emailError = true
            emailErrorMessage = "Last name is required."
            return false
        }
        return true
    }

suspend fun validateUsername(): Boolean {
    if (username.isBlank()) {
        usernameError = true
        usernameErrorMessage = "Username is required."
        return false
    }

    val usernameExists = userProfileViewModel.isUsernameTaken(username)
    return if (usernameExists) {
        usernameError = true
        usernameErrorMessage = "Username already taken. Please choose another."
        false
    } else {
        true
    }
}

    fun validatePassword(): Boolean {
        if (password.isBlank()) {
            passwordError = true
            passwordErrorMessage = "Password is required"
            return false
        }
        return true
    }

    suspend fun performSignUp() {

        val usernameValid = validateUsername()
        val emailValid = validateEmail()
        val phoneNumberValid = validatePhoneNumber()
        val firstNameValid = validateFirstName()
        val lastNameValid = validateLastName()
        val passwordValid = validatePassword()

        val allValid = usernameValid && emailValid && phoneNumberValid && firstNameValid && lastNameValid && passwordValid
        if (!allValid) {
            return
        }

        // Combine first and last name
        val fullName = "$firstName $lastName".trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-up successful
                    Log.d("SignUp", "createUserWithEmail:success")
                    val firebaseUser = auth.currentUser

                    val userProfile = UserProfile(
                        id = firebaseUser?.uid ?: "",
                        name = fullName,
                        email = email,
                        phoneNumber = phoneNumber,
                        userName = username,
                        password = password,
                        genrePreferences = mutableListOf()
                    )
                    userProfileViewModel.addUserProfile(userProfile)
                    navController.navigate(Screens.UserPreferences.screen) {
                        popUpTo(Screens.Entry.screen) { inclusive = true }
                    }
                } else {
                    // Sign-up failed
                    Log.w("SignUp", "createUserWithEmail:failure", task.exception)
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    when {
                        exceptionMessage.contains("The email address is badly formatted") -> {
                            emailError = true
                            emailErrorMessage = "Please enter a correctly formatted email address"
                        }
                        exceptionMessage.contains("The email address is already in use by another account") -> {
                            emailError = true
                            emailErrorMessage = "This email address is already in use"
                        }
                        exceptionMessage.contains("PASSWORD_DOES_NOT_MEET_REQUIREMENTS", ignoreCase = true) -> {
                            passwordError = true
                            passwordErrorMessage = "Password must be at least 12 characters long and include: One uppercase letter: One lowercase letter, One number, One special character"
                        }
                    }

                }
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
        if (firstNameError) {
            Text(
                text = firstNameErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // LAST NAME
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

        // EMAIL
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

        // PHONE NUMBER
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

        // USERNAME
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (it.isNotBlank()) usernameError = false
            },
            label = { Text("Username") },
            isError = usernameError,
            modifier = Modifier.fillMaxWidth(),
        )
        if (usernameError) {
            Text(
                text = usernameErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (it.isNotBlank()) passwordError = false
            },
            label = { Text("Password") },
            isError = passwordError,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible}) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.Lock else Icons.Filled.Lock,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"

                    )
                }
            }
        )
        if (passwordError) {
            Text(
                text = passwordErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (firebaseErrorMessage.isNotEmpty()) {
            Text(
                text = firebaseErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Complete Sign Up Button
        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                scope.launch {
                    userProfileViewModel.viewModelScope.launch {
                        performSignUp()
                    }
                }
                      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Complete Sign Up")
        }
    }
}