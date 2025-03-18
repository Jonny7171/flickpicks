package com.example.flickpicks.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flickpicks.ui.theme.BlueNew
import com.example.flickpicks.ui.viewmodels.UserSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val userSearchViewModel: UserSearchViewModel = viewModel()
    var requestStatus by remember { mutableStateOf("") }

    fun performSearch() {
        keyboardController?.hide()
        focusManager.clearFocus()
        hasSearched = true
        userSearchViewModel.searchUsers(searchText)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search Users") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search Users", fontSize = 18.sp, color = BlueNew) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { performSearch() }),
                singleLine = true
            )

            Button(
                onClick = { performSearch() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display "User not found" if no results
            if (hasSearched && userSearchViewModel.userList.value.isEmpty() && searchText.isNotBlank()) {
                Text(text = "User not found", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (requestStatus.isNotEmpty()) {
                Text(text = requestStatus, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Display users
            LazyColumn {
                items(userSearchViewModel.userList.value) { user ->
                    SearchUserItem(user = user, onSendRequest = { targetUser ->
                        userSearchViewModel.sendFriendRequest(targetUser) { success, message ->
                            requestStatus = message
                            Toast.makeText(context, requestStatus, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun SearchUserItem(
    user: com.example.flickpicks.data.model.UserProfile,
    onSendRequest: (com.example.flickpicks.data.model.UserProfile) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // Display usernames
        Text(text = user.userName, fontSize = 24.sp)
        Button(
            onClick = { onSendRequest(user) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Friend Request")
        }
    }
}
