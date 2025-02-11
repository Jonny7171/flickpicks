package com.example.flickpicks.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.data.model.UserProfile

val followingList = listOf(
    UserProfile(1, "Alice Johnson", "https://via.placeholder.com/150"),
    UserProfile(2, "Bob Smith", "https://via.placeholder.com/150"),
    UserProfile(3, "Charlie Brown", "https://via.placeholder.com/150")
)

val followersList = listOf(
    UserProfile(4, "David Miller", "https://via.placeholder.com/150"),
    UserProfile(5, "Emma Wilson", "https://via.placeholder.com/150"),
    UserProfile(6, "Frankie Thomas", "https://via.placeholder.com/150")
)

val friendRequests = listOf(
    UserProfile(7, "Grace Lee", "https://via.placeholder.com/150"),
    UserProfile(8, "Henry Carter", "https://via.placeholder.com/150")
)


@Composable
fun Friends() {
    var selectedTab by remember { mutableStateOf("Following") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Following", "Followers", "Requests").forEach { tab ->
                Button(
                    onClick = { selectedTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = tab,
                        color = if (selectedTab == tab) Color.White else Color.Black
                    )
                }
            }
        }

        when (selectedTab) {
            "Following" -> UserList(followingList, buttonText = "Unfollow") { /* Handle Unfollow */ }
            "Followers" -> UserList(followersList, buttonText = "Remove") { /* Handle Remove Follower */ }
            "Requests" -> UserList(friendRequests, buttonText1 = "Accept", buttonText2 = "Decline") { /* Handle Accept/Decline */ }
        }
    }
}

@Composable
fun UserList(users: List<UserProfile>, buttonText: String? = null, buttonText1: String? = null, buttonText2: String? = null, onButtonClick: (UserProfile) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        items(users) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(user.profilePicUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = user.name,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                if (buttonText != null) {
                    Button(onClick = { onButtonClick(user) }) {
                        Text(text = buttonText)
                    }
                } else if (buttonText1 != null && buttonText2 != null) {
                    Row {
                        Button(onClick = { onButtonClick(user) }) {
                            Text(text = buttonText1)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(onClick = { onButtonClick(user) }) {
                            Text(text = buttonText2, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriends() {
    Friends()
}