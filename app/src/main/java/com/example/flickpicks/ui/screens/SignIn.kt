package com.example.flickpicks.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun SignIn(navController: NavController) {
    // Field values
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Error states
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    var auth = FirebaseAuth.getInstance()
    var firebaseErrorMessage by remember { mutableStateOf("") }
    var userDetails by remember { mutableStateOf<String>("") }


    fun validateFields() {
        emailError = email.isBlank()
        passwordError = password.isBlank()
    }

    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            userDetails = "Signed in as: ${user.email}"
            navController.navigate(Screens.MyFeed.screen) {
                popUpTo(Screens.Entry.screen) { inclusive = true }
            }
        } else {
            userDetails = "Please try again or check your credentials."
        }

    }

    fun performSignIn() {
        validateFields()
        if (emailError || passwordError) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    Log.d("SignIn", "signInWithEmail:success")
                    val user = auth.currentUser
                    navController.navigate(Screens.MyFeed.screen) {
                        popUpTo(Screens.Entry.screen) { inclusive = true }
                    }
                } else {
                    // If sign-in fails, display a message to the user
                    Log.w("SignIn", "signInWithEmail:failure", task.exception)
                    firebaseErrorMessage = "Authentication failed"
                    updateUI(null)
                }
            }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (it.isNotBlank()) {
                    emailError = false
                }
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

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (it.isNotBlank()) {
                    passwordError = false
                }
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

        Button(
            onClick = { performSignIn() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        if (firebaseErrorMessage.isNotEmpty()) {
            Text(
                text = firebaseErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Display user details after successful sign-in
        if (userDetails.isNotEmpty()) {
            Text(
                text = userDetails,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
