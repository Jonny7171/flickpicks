package com.example.flickpicks.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flickpicks.ui.theme.BlueNew

@Composable
fun Search() {
    var searchText by remember { mutableStateOf("") }
    var movieResults by remember { mutableStateOf<List<String>>(emptyList()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    fun performSearch() {
        keyboardController?.hide()
        focusManager.clearFocus()

        movieResults = listOf(
            "Baby Driver",
            "Back To The Future",
            "Back To The Future 2",
            "Inception",
            "Interstellar"
        ).filter { it.contains(searchText, ignoreCase = true) }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search Movies", fontSize = 18.sp, color = BlueNew) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    performSearch()
                }
            ),
            singleLine = true
        )

        Button(
            onClick = { performSearch() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        if (movieResults.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            movieResults.forEach { movie ->
                Text(
                    text = movie,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}