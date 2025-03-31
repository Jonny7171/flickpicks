package com.example.flickpicks.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Friends(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    // Observe the user profile state
    val currentUserState = userProfileViewModel.userProfile
    val currentUser = currentUserState.value

    val auth = FirebaseAuth.getInstance()

    // Ensure the current user's profile is fetched
    LaunchedEffect(auth.currentUser?.uid) {
        val uid = auth.currentUser?.uid
        if (uid != null && currentUser == null) {
            userProfileViewModel.fetchUserProfile(uid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friends") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screens.UserSearch.screen)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Users"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", fontSize = 18.sp)
            }
        } else {
            var selectedTab by remember { mutableStateOf("Friends") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tabs: "Friends" and "Requests"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Friends", "Requests").forEach { tab ->
                        Button(
                            onClick = { selectedTab = tab },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == tab)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = tab,
                                color = if (selectedTab == tab) Color.White else Color.Black
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                when (selectedTab) {
                    "Friends" -> {
                        val updatedFriendIds = currentUser.followers
                        FriendsList(
                            friendIds = updatedFriendIds,
                            onRemove = { friendId ->
                                userProfileViewModel.removeFriend(friendId)
                            }
                        )
                    }
                    "Requests" -> {
                        val updatedRequests = currentUser.incomingRequests
                        RequestsList(
                            incomingRequests = updatedRequests,
                            onAccept = { requestUserId ->
                                userProfileViewModel.acceptFriendRequest(requestUserId)
                            },
                            onDecline = { requestUserId ->
                                userProfileViewModel.declineFriendRequest(requestUserId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendsList(
    friendIds: List<String>,
    onRemove: (String) -> Unit
) {
    if (friendIds.isEmpty()) {
        Text(
            text = "No friends found.",
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(friendIds) { friendId ->
                val friendName by produceState(initialValue = "Loading...", key1 = friendId) {
                    val repository = com.example.flickpicks.data.repository.UserProfileRepository(
                        com.example.flickpicks.data.repository.UserProfileFirestoreDatabase()
                    )
                    val profile = try {
                        repository.getUserProfile(friendId)
                    } catch (e: Exception) {
                        null
                    }
                    value = profile?.userName ?: "Unknown"
                }
                FriendItem(friendId = friendId, friendName = friendName, onRemove = onRemove)
            }
        }
    }
}

@Composable
fun FriendItem(
    friendId: String,
    friendName: String,
    onRemove: (String) -> Unit
) {
    val friendProfilePic by produceState(initialValue = "", key1 = friendId) {
        val repository = com.example.flickpicks.data.repository.UserProfileRepository(
            com.example.flickpicks.data.repository.UserProfileFirestoreDatabase()
        )
        val profile = try {
            repository.getUserProfile(friendId)
        } catch (e: Exception) {
            null
        }
        value = profile?.profilePicUrl ?: ""
    }
    val avatarMap = mapOf(
        "dog" to com.example.flickpicks.R.drawable.dog,
        "cat" to com.example.flickpicks.R.drawable.cat,
        "glasses" to com.example.flickpicks.R.drawable.glassses,
        "miami" to com.example.flickpicks.R.drawable.miami
    )
    val isAvatar = avatarMap.containsKey(friendProfilePic)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isAvatar) {
            Image(
                painter = androidx.compose.ui.res.painterResource(
                    avatarMap[friendProfilePic] ?: com.example.flickpicks.R.drawable.dog
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        } else {
            Image(
                painter = if (friendProfilePic.isNotBlank())
                    rememberAsyncImagePainter(friendProfilePic)
                else
                    rememberAsyncImagePainter("https://via.placeholder.com/150"),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = friendName,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Button(onClick = { onRemove(friendId) }) {
            Text("Remove")
        }
    }
}

@Composable
fun RequestsList(
    incomingRequests: List<String>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    if (incomingRequests.isEmpty()) {
        Text(
            text = "No friend requests.",
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(incomingRequests) { requestUserId ->
                FriendRequestItem(
                    requestUserId = requestUserId,
                    onAccept = onAccept,
                    onDecline = onDecline
                )
            }
        }
    }
}
@Composable
fun FriendRequestItem(
    requestUserId: String,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    val friendProfilePic by produceState(initialValue = "", key1 = requestUserId) {
        val repository = com.example.flickpicks.data.repository.UserProfileRepository(
            com.example.flickpicks.data.repository.UserProfileFirestoreDatabase()
        )
        val profile = try {
            repository.getUserProfile(requestUserId)
        } catch (e: Exception) {
            null
        }
        value = profile?.profilePicUrl ?: ""
    }
    val userName by produceState(initialValue = "Loading...", key1 = requestUserId) {
        val repository = com.example.flickpicks.data.repository.UserProfileRepository(
            com.example.flickpicks.data.repository.UserProfileFirestoreDatabase()
        )
        val profile = try {
            repository.getUserProfile(requestUserId)
        } catch (e: Exception) {
            null
        }
        value = profile?.userName ?: "Unknown"
    }
    val avatarMap = mapOf(
        "dog" to com.example.flickpicks.R.drawable.dog,
        "cat" to com.example.flickpicks.R.drawable.cat,
        "glasses" to com.example.flickpicks.R.drawable.glassses,
        "miami" to com.example.flickpicks.R.drawable.miami
    )
    val isAvatar = avatarMap.containsKey(friendProfilePic)
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAvatar) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(
                        avatarMap[friendProfilePic] ?: com.example.flickpicks.R.drawable.dog
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            } else {
                Image(
                    painter = if (friendProfilePic.isNotBlank())
                        rememberAsyncImagePainter(friendProfilePic)
                    else
                        rememberAsyncImagePainter("https://via.placeholder.com/150"),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "User: $userName", fontSize = 20.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (!isProcessing) {
                        isProcessing = true
                        onAccept(requestUserId)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isProcessing
            ) {
                Text("Accept")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (!isProcessing) {
                        isProcessing = true
                        onDecline(requestUserId)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isProcessing
            ) {
                Text("Decline")
            }
        }
    }
}