package com.example.flickpicks.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.flickpicks.ui.viewmodels.PartyGroupViewModel

@Composable
fun PartyGroup(navController: NavController, groupId: Int, viewModel: PartyGroupViewModel = hiltViewModel()){
    var selectedTab by remember {mutableStateOf(0)}
    val tabTitles = listOf("Schedule Time", "Movie Recs")

    val partyGroup by viewModel.partyGroup.collectAsState()
    LaunchedEffect(groupId) {
        viewModel.loadPartyGroup(groupId)

    }


    //val partyGroup by viewModel.getPartyGroup(groupId).collectAsState(initial = null)
    Column(modifier = Modifier.fillMaxSize()
        .padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(onClick = { navController.popBackStack() }) {
                Icon( imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text=partyGroup?.groupName ?: "Loading...", style = MaterialTheme.typography.titleLarge)


            IconButton(onClick = { navController.navigate(Screens.MemberSearch.createRoute(groupId.toString())) }) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Show Members")
            }


        }

        Spacer(modifier= Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx},
                    text = { Text(title) }
                )
            }



        }

        when (selectedTab) {
            0 -> ScheduleTimeTab(PartyGroupViewModel(), groupId)
            1 -> MovieRecsTab()
        }

    }

}


@Composable
fun ScheduleTimeTab(viewModel: PartyGroupViewModel, groupId: Int) {
    val selectedDays by viewModel.selectedDays.collectAsState()
    val selectedTimes by viewModel.selectedTimes.collectAsState()
    var bestTime by remember { mutableStateOf("Click to Find Best Time") }
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val timeSlots = (1..12).map { "$it:00 AM" } + listOf("12:00 PM") + (1..11).map { "$it:00 PM" } + listOf("12:00 AM")

    LaunchedEffect(groupId) {
        viewModel.loadPartyGroup(groupId)
        viewModel.findBestTime(groupId) { time -> bestTime = time }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Select Available Days", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))


        Column(modifier = Modifier.weight(1f)) {
            LazyColumn {
                items(daysOfWeek) { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { viewModel.toggleDaySelection(groupId, day) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(day, fontSize = 18.sp)
                        if (selectedDays.contains(day)) {
                            Text("✔", fontSize = 18.sp, color = Color.Blue)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Select Available Times", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn {
                items(timeSlots) { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { viewModel.toggleTimeSelection(groupId, time) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(time, fontSize = 18.sp)
                        if (selectedTimes.contains(time)) {
                            Text("✔", fontSize = 18.sp, color = Color.Blue)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { viewModel.findBestTime(groupId) { bestTime = it } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find Best Time")
        }

        // Display Best Time
        Text(text = bestTime, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
    }
}


@Composable
fun MovieRecsTab() {
    Column (modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("Movie Tinder!", style = MaterialTheme.typography.titleMedium)
    }

}