package com.example.flickpicks.ui.screens

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flickpicks.data.model.ChatMessage
import com.example.flickpicks.ui.viewmodels.PartyGroupViewModel

@Composable
fun ChatScreen(navController: NavController, viewModel: PartyGroupViewModel = hiltViewModel(), groupId: Int) {
    var message by remember { mutableStateOf("") }

    val messages by viewModel.messages.collectAsState()
    val userName by viewModel.currentUserName.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.loadMessages(groupId)
        viewModel.loadCurrentUserName()
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Group Chat", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                ChatBubble(msg, currentUserName = userName?: "Unknown")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        viewModel.sendMessage(
                            groupId,
                            ChatMessage(sender = userName ?: "Unknown", message = message, isMe = true, timestamp = System.currentTimeMillis())
                        )
                        message = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }


}

@Composable
fun ChatBubble(chatMessage: ChatMessage, currentUserName: String) {
    val isMe = chatMessage.sender == currentUserName
    val bubbleColor = if (isMe) Color(0xFF34C759) else Color.LightGray
    val textColor = if (isMe) Color.White else Color.Black
    val shape = if (isMe) RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp) else RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(bubbleColor, shape)
                .padding(12.dp)
                .clip(shape)
        ) {
            Text(
                text = "${chatMessage.sender} • ${formatTimestamp(chatMessage.timestamp)}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = chatMessage.message,
                fontSize = 16.sp,
                color = textColor,
                textAlign = if (isMe) TextAlign.End else TextAlign.Start
            )
        }
    }
}

fun formatTimestamp(timestamp: Long?): String {
    return if (timestamp != null) {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        sdf.format(java.util.Date(timestamp))
    } else {
        ""
    }
}

