package com.example.flickpicks.ui.screens

import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter
import com.example.flickpicks.ui.viewmodels.PartyGroupViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PartyGroup(navController: NavController, groupId: Int, viewModel: PartyGroupViewModel = hiltViewModel()){
    var selectedTab by remember {mutableStateOf(0)}
    val tabTitles = listOf("Schedule Time", "Play","Movie Recs")

    val partyGroup by viewModel.partyGroup.collectAsState()
    LaunchedEffect(groupId) {
        viewModel.loadPartyGroup(groupId)

    }

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
            0 -> ScheduleTimeTab(viewModel, groupId)
            1 -> MovieRecsTab(viewModel, groupId, navController)
            2 -> RecommendationTab(viewModel, groupId)
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
fun MovieRecsTab(viewModel: PartyGroupViewModel, groupId: Int, navController: NavController) {
    val partyGroup by viewModel.partyGroup.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val gameActive = partyGroup?.gameActive == true
    var showVotingUI by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableStateOf(0) }

    val suggestions = partyGroup?.genreMovieSuggestions ?: emptyList()
    val votedMovieIds = partyGroup?.usersVoted?.get(userId).orEmpty().toSet()
    val remainingMovies = suggestions.filterNot { votedMovieIds.contains(it.id) }
    val currentMovie = remainingMovies.getOrNull(currentIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            userId?.let {
                viewModel.startNewGame(groupId, it)
                showVotingUI = false
            }
        }) {
            Text("Start New Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                userId?.let {
                    viewModel.resumeExistingGame(groupId, it)
                    showVotingUI = true
                    currentIndex = 0
                }
            },
            enabled = gameActive
        ) {
            Text("Resume Current Game")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            if (gameActive) "A game is in progress. Click above to join!"
            else "No game is currently active. Start one to play!",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (showVotingUI) {
            if (remainingMovies.isNotEmpty() && currentMovie != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text("Vote for a Movie", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Image(
                            painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${currentMovie.poster_path}"),
                            contentDescription = currentMovie.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(currentMovie.title, style = MaterialTheme.typography.titleSmall)
                        Text(currentMovie.overview, maxLines = 3)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                userId?.let {
                                    viewModel.voteOnMovie(groupId, it, currentMovie.id, false)
                                    currentIndex++
                                }
                            }) {
                                Text("Don't Wanna Watch")
                            }
                            Button(onClick = {
                                userId?.let {
                                    viewModel.voteOnMovie(groupId, it, currentMovie.id, true)
                                    currentIndex++
                                }
                            }) {
                                Text("Watch")
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            } else {
                Text("You've voted on all movies!", style = MaterialTheme.typography.bodyLarge)
            }
        }



    }
}
@Composable
fun RecommendationTab(viewModel: PartyGroupViewModel, groupId: Int) {
    val partyGroup by viewModel.partyGroup.collectAsState()
    val winnerMovie = partyGroup?.winnerMovie

    LaunchedEffect( groupId, winnerMovie?.id ) {
        viewModel.loadPartyGroup(groupId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Winner Recommendation", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (winnerMovie?.id?.isNotEmpty() == true) {
            Image(
                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${winnerMovie.poster_path}"),
                contentDescription = winnerMovie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(winnerMovie.title, style = MaterialTheme.typography.titleMedium)
            Text(winnerMovie.overview, style = MaterialTheme.typography.bodyMedium)
        } else {
            Text("Waiting for all members to finish voting...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

