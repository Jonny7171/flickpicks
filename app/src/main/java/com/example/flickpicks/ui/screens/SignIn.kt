package com.example.flickpicks.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flickpicks.ui.viewmodels.SignInViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun SignIn(navController: NavController, viewModel: SignInViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false)}

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var emailErrorMessage  by remember { mutableStateOf("")}
    var passwordErrorMessage by remember { mutableStateOf("")}

    var auth = FirebaseAuth.getInstance()
    var userDetails by remember { mutableStateOf<String>("") }


    fun validateEmail(): Boolean {
        if (email.isBlank()) {
            emailError = true
            emailErrorMessage = "Email address is required"
            return false
        }
        return true
    }

    fun validatePassword() : Boolean {
        if (password.isBlank()) {
            passwordError = true
            passwordErrorMessage = "Password is required"
            return false
        }
        return true
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
        validateEmail()
        validatePassword()
        if (emailError || passwordError) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    Log.d("SignIn", "signInWithEmail:success")
                    val user = auth.currentUser

                    user?.let {
                        viewModel.saveSession(it.uid, it.email ?: "")
                        updateUI(it)
                    }

                    navController.navigate(Screens.MyFeed.screen) {
                        popUpTo(Screens.Entry.screen) { inclusive = true }
                    }
                } else {
                    Log.w("SignIn", "signInWithEmail:failure", task.exception)
                    val exceptionMessage = task.exception?.message ?: "Unknown error"
                    when {
                        exceptionMessage.contains("The email address is badly formatted") -> {
                            emailError = true
                            emailErrorMessage = "Please enter a correctly formatted email address"
                        }
                        exceptionMessage.contains("The supplied auth credential is incorrect, malformed or has expired") -> {
                            passwordError = true
                            passwordErrorMessage = "Please enter the password associated with this account"
                        }
                    }
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
                text = emailErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

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

        Button(
            onClick = { performSignIn() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }
    }
}
