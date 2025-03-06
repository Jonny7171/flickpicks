package com.example.flickpicks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.flickpicks.data.model.PartyGroup
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures

val mockGroups = listOf(
    PartyGroup(1, "Charlie's Angels"),
    PartyGroup(2, "Movie Mingle"),
    PartyGroup(3, "Popcorn & Chill")
)

@Composable
fun Party(navController: NavController){
    var userMadeGroups by remember {mutableStateOf(mockGroups.toMutableList())}
    var newGroupName by remember {mutableStateOf("")}
    var showDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(text = "Movie Party", fontSize = 24.sp)

            Button(onClick = {},
                modifier = Modifier.size(40.dp),
                shape = CircleShape,

                contentPadding = PaddingValues(0.dp)

            ) {
                Text(text = "+", fontSize = 24.sp, modifier = Modifier.clickable { showDialog = true})
            }

        }

        Spacer(modifier=Modifier.height(8.dp))
        ShowGroups(userMadeGroups, navController, onDelete = { group ->
            userMadeGroups = userMadeGroups.filter{it.id != group.id }.toMutableStateList()
        })

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Create New Group") },
                text = {
                    Column {
                        TextField(
                            value = newGroupName,
                            onValueChange = {
                                newGroupName = it
                                showError = false
                            },
                            label = { Text("Group Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (showError) {
                            Text("Group name cannot be empty", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(4.dp))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newGroupName.isNotBlank()) {
                                val newGroup = PartyGroup(id = userMadeGroups.size + 1, name = newGroupName)
                                userMadeGroups = (userMadeGroups + newGroup).toMutableList()
                                newGroupName = ""
                                showDialog = false
                            } else {
                                showError = true
                            }
                        }
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }


}

@Composable
fun ShowGroups(groups: List<PartyGroup>, navController: NavController, onDelete: (PartyGroup) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf<PartyGroup?>(null)}
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(4.dp)) {
            items(groups) { group ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate(Screens.PartyGroup.screen)}
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {

                        detectTapGestures(
                            onTap = {
                                navController.navigate(Screens.PartyGroup.screen)
                            },
                            onLongPress = {
                                showDeleteDialog = group
                                //onDelete(group)
                            }
                        )
                    }


                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = group.name,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = { navController.navigate(Screens.PartyGroupChat.screen)} ) {
                            Text(text = "Send Message")
                        }

                    }

                }


            }
        }
    }

    showDeleteDialog?.let { group ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Group") },
            text = { Text("Are you sure you want to delete \"${group.name}\"?") },
            confirmButton = {
                Button(onClick = {
                    onDelete(group)
                    showDeleteDialog = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }

            }
        )
    }
}

