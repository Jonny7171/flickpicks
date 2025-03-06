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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flickpicks.ui.viewmodels.PartyGroupViewModel

@Composable
fun PartyGroup(navController: NavController){
    var selectedTab by remember {mutableStateOf(0)}
    var tabTitles = listOf("Past Movies", "Schedule Time", "Movie Recs")


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
            Text(text="Charlie's Angels", style = MaterialTheme.typography.titleLarge)

        }

        Spacer(modifier= Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        /*
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly

        ){

            Text(
                text= "Past Movies",
                modifier = Modifier.padding(8.dp)
                .clickable {  }
            )
            Text(
                text= "Schedule Time",
                modifier = Modifier.padding(8.dp)
                    .clickable {  }
            )
            Text(
                text= "Movie Recs",
                modifier = Modifier.padding(8.dp)
                .clickable {  }
            )
            */

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
            0 -> PastMoviesTab()
            1 -> ScheduleTimeTab(PartyGroupViewModel())
            2 -> MovieRecsTab()
        }

    }

}

@Composable
fun ScheduleTimeTab(viewModel: PartyGroupViewModel) {
    val availableTimes = listOf("6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM")
    var bestTime by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("Select Available Times", fontSize = 20.sp, modifier = Modifier.padding(8.dp))

        LazyColumn {
            items(availableTimes) { time ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectTime(time)
                        }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(time, fontSize = 16.sp)
                    if (viewModel.selectedTimes.contains(time)) {
                        Text("✔")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { bestTime = findBestTime(viewModel.selectedTimes) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Find Best Time")
        }

        bestTime?.let {
            Text("Best Time: $it", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
        }
    }
}

fun findBestTime(selectedTimes: List<String>): String {
    val timeFrequency = selectedTimes.groupingBy { it }.eachCount()
    return timeFrequency.maxByOrNull { it.value }?.key ?: "No consensus"
}


@Composable
fun PastMoviesTab() {
    Column (modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("No Movies Watched Yet!", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun MovieRecsTab() {
    Column (modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("Movie Tinder!", style = MaterialTheme.typography.titleMedium)
    }

}