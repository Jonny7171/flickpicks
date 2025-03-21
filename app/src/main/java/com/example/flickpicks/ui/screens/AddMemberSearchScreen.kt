

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flickpicks.ui.theme.BlueNew
import com.example.flickpicks.ui.viewmodels.AddMemberViewModel
import com.example.flickpicks.ui.viewmodels.UserSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSearchScreen(navController: NavController, groupId: Int, viewModel: AddMemberViewModel = hiltViewModel()) {
    var searchText by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val userSearchViewModel: UserSearchViewModel = viewModel()
    val requestStatus by remember { mutableStateOf("") }


    val partyGroup by viewModel.partyGroup.collectAsState() // Get party group detail
    //val userList by viewModel.userList.collectAsState()
    val memberNames by viewModel.memberNames.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.loadPartyGroup(groupId) // Load existing members

    }

    fun performSearch() {
        keyboardController?.hide()
        focusManager.clearFocus()
        hasSearched = true
        userSearchViewModel.searchUsers(searchText) // Fetch users from Firestore
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Members") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Text("Current Members", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn {
                items(partyGroup?.members ?: emptyList()) { memberId ->
                    val memberName = memberNames[memberId] ?: "Loading..."
                    Text(text = memberName, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
                    //Text(text = memberId, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add Members",
                fontSize = 20.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(bottom = 8.dp)
            )


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


            if (hasSearched && userSearchViewModel.userList.value.isEmpty()  && searchText.isNotBlank()) {
                Text(text = "User not found", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }


            if (requestStatus.isNotEmpty()) {
                Text(text = requestStatus, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }


            LazyColumn {
                items(userSearchViewModel.userList.value) { user ->
                    SearchMemberItem(user = user, onSendRequest = { targetUser ->
                        viewModel.addMemberToGroup(groupId, targetUser.id) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }


        }
    }
}



@Composable
fun SearchMemberItem(
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
            Text("Add Member")
        }
    }
}
