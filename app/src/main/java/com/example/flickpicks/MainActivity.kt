package com.example.flickpicks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flickpicks.ui.screens.ChatScreen
import com.example.flickpicks.ui.screens.EditProfile
import com.example.flickpicks.ui.screens.UserSearchScreen
import com.example.flickpicks.ui.screens.Entry
import com.example.flickpicks.ui.screens.Friends
import com.example.flickpicks.ui.screens.MemberSearchScreen
import com.example.flickpicks.ui.screens.MovieDetailScreen
import com.example.flickpicks.ui.screens.MyFeed
import com.example.flickpicks.ui.screens.Party
import com.example.flickpicks.ui.screens.PartyGroup
import com.example.flickpicks.ui.screens.Profile
import com.example.flickpicks.ui.screens.Screens
import com.example.flickpicks.ui.screens.Search
import com.example.flickpicks.ui.screens.Settings
import com.example.flickpicks.ui.screens.SignIn
import com.example.flickpicks.ui.screens.SignUp
import com.example.flickpicks.ui.screens.UserPreferences
import com.example.flickpicks.ui.theme.BlueNew
import com.example.flickpicks.ui.theme.FlickPicksTheme
import com.example.flickpicks.ui.viewmodels.AddMemberViewModel
import com.example.flickpicks.ui.viewmodels.PartyGroupViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import com.example.flickpicks.data.repository.UserSessionRepository
import com.example.flickpicks.data.database.Session
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionRepository: UserSessionRepository

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        setContent {
            FlickPicksTheme {

                val sessionState = remember { mutableStateOf<Session?>(null) }

                LaunchedEffect(Unit) {
                    sessionState.value = sessionRepository.getSession()
                }
                if (sessionState.value != null || sessionState.value == null) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        BottomNavigationBar(
                            startDestination = if (sessionState.value != null) {
                                Screens.MyFeed.screen
                            } else {
                                Screens.Entry.screen
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun BottomNavigationBar(startDestination: String) {
        val navigationController = rememberNavController()
        val selected = remember { mutableStateOf(Icons.Default.Menu) }
        val currentBackStackEntry = navigationController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry.value?.destination?.route
        val shouldShowBottomBar =
            currentRoute != Screens.Entry.screen && currentRoute != Screens.SignUp.screen && currentRoute != Screens.SignIn.screen

        when (currentRoute) {
            Screens.Search.screen -> selected.value = Icons.Default.Search
            Screens.Friends.screen -> selected.value = Icons.Default.Face
            Screens.MyFeed.screen -> selected.value = Icons.Default.Menu
            Screens.Party.screen -> selected.value = Icons.Default.MailOutline
            Screens.Profile.screen -> selected.value = Icons.Default.AccountCircle
        }

        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar) {
                    BottomAppBar(containerColor = BlueNew) {
                        IconButton(
                            onClick = {
                                selected.value = Icons.Default.Search
                                navigationController.navigate(Screens.Search.screen) {
                                    popUpTo(0)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = if (selected.value == Icons.Default.Search) Color.hsl(
                                    133F, 1F, 0.38F
                                ) else Color.White
                            )
                        }
                        IconButton(
                            onClick = {
                                selected.value = Icons.Default.Face
                                navigationController.navigate(Screens.Friends.screen) {
                                    popUpTo(0)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = if (selected.value == Icons.Default.Face) Color.hsl(
                                    133F, 1F, 0.38F
                                ) else Color.White
                            )
                        }
                        IconButton(
                            onClick = {
                                selected.value = Icons.Default.Menu
                                navigationController.navigate(Screens.MyFeed.screen) {
                                    popUpTo(0)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = if (selected.value == Icons.Default.Menu) Color.hsl(
                                    133F, 1F, 0.38F
                                ) else Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                selected.value = Icons.Default.MailOutline
                                navigationController.navigate(Screens.Party.screen) {
                                    popUpTo(0)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.MailOutline,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = if (selected.value == Icons.Default.MailOutline) Color.hsl(
                                    133F, 1F, 0.38F
                                ) else Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                selected.value = Icons.Default.AccountCircle
                                navigationController.navigate(Screens.Profile.screen) {
                                    popUpTo(0)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = if (selected.value == Icons.Default.AccountCircle) Color.hsl(
                                    133F, 1F, 0.38F
                                ) else Color.White
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navigationController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screens.Entry.screen) {
                    Entry(navController = navigationController)
                }
                composable(Screens.SignUp.screen) {
                    SignUp(navController = navigationController)
                }
                composable(Screens.UserSearch.screen) {
                    UserSearchScreen(navController = navigationController)
                }
                composable(Screens.SignIn.screen) {
                    SignIn(navController = navigationController)
                }
                composable(Screens.MyFeed.screen) {
                    MyFeed(navController = navigationController)
                }
                composable(Screens.UserPreferences.screen) {
                    UserPreferences(navController = navigationController)
                }
                composable(Screens.Search.screen) { Search(navController = navigationController) }
                composable(Screens.Friends.screen) {
                    Friends(navController = navigationController)
                }
                composable(Screens.Party.screen) {
                    Party(navController = navigationController)
                }
                composable(Screens.Profile.screen) {
                    Profile(navController = navigationController)
                }
                composable(Screens.EditProfile.screen) {
                    EditProfile(navController = navigationController)
                }
                composable(Screens.Settings.screen) {
                    Settings(navController = navigationController)
                }
                composable(Screens.PartyGroup.screen + "/{groupId}") { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull() ?: 0
                    PartyGroup(navController = navigationController, groupId)
                }

                composable(Screens.PartyGroupChat.screen + "/{groupId}") { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
                    if (groupId != null) {
                        val partyGroupViewModel: PartyGroupViewModel = hiltViewModel()
                        ChatScreen(
                            navController = navigationController,
                            viewModel = partyGroupViewModel,
                            groupId = groupId
                        )
                    }
                }
                composable(Screens.MemberSearch.screen) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
                    if (groupId != null) {
                        val addMemberViewModel: AddMemberViewModel = hiltViewModel()
                        MemberSearchScreen(
                            navController = navigationController,
                            groupId = groupId,
                            addMemberViewModel
                        )
                    }
                }

                composable(Screens.MovieDetail.screen) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getString("movieId")
                    if (movieId != null) {
                        MovieDetailScreen(movieId, navigationController)
                    }
                }
            }
        }


    }
}
