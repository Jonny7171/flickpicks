package com.example.flickpicks

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flickpicks.data.model.Genre
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.ui.screens.EditProfile
import com.example.flickpicks.ui.screens.Entry
import com.example.flickpicks.ui.screens.Friends
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
import com.example.flickpicks.ui.screens.mockReviews
import com.example.flickpicks.ui.theme.BlueNew
import com.example.flickpicks.ui.theme.FlickPicksTheme
import com.example.flickpicks.ui.theme.GreenJC
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import com.example.flickpicks.ui.viewmodels.GenreViewModel
import com.example.flickpicks.ui.viewmodels.MovieReviewViewModel
import com.example.flickpicks.ui.viewmodels.UserProfileViewModel
import com.example.flickpicks.ui.viewmodels.PartyGroupViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private val genreViewModel: GenreViewModel by viewModels()
    private val partyGroupViewModel: PartyGroupViewModel by viewModels()
    private val movieReviewViewModel: MovieReviewViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val sampleUser = UserProfile(
            id = 5,
            name = "Shaili Doe",
            email = "Jim.doe@example.com",
            userName = "jimdoe"
        )

        val sampleGenre = Genre(name = "Sad", count = 6)

        val samplePartyGroup =
            PartyGroup(id = 5, name = "CS Party")

        val sampleReview = MovieReview(
            id = 104,
            movieTitle = "B99",
            release_date = "2010-07-16",
            tagline = "Your mind is the scene of the crime.",
            overview = "A thief who enters the dreams of others.",
            genres = listOf("Sci-Fi", "Thriller"),
            reviewerName = "Alice",
            reviewText = "Amazing movie!",
            rating = 5,
            streamingPlatform = "Netflix"
        )

        /*
        // Using the genreViewModel for CRUD
        genreViewModel.addGenre(sampleGenre)
        genreViewModel.getGenre(sampleGenre.name)
        val updates = mapOf("count" to 15)
        genreViewModel.updateGenre(sampleGenre, updates)
        genreViewModel.deleteGenre(sampleGenre)

        */

        /*
        // Using the movieReviewModel for CRUD
        movieReviewViewModel.addMovieReview(sampleReview)
        movieReviewViewModel.getMovieReview(sampleReview.id)
        movieReviewViewModel.deleteMovieReview(sampleReview)
        val updates = mapOf("overview" to "A detective with a harsh boss", "reviewText" to "Best detective show")
        movieReviewViewModel.updateMovieReview(sampleReview, updates)
        */

        /*
        // Using the userProfileModel for CRUD
        userProfileViewModel.addUserProfile(sampleUser)
        userProfileViewModel.getUserProfile(sampleUser.id)
        userProfileViewModel.deleteUserProfile(sampleUser)
        val updates = mapOf("email" to "sk@gmail.com", "username" to "SK")
        userProfileViewModel.updateUserProfile(sampleUser, updates)
        */

        /*
        // Using the partyGroupModel for CRUD
        partyGroupViewModel.addPartyGroup(samplePartyGroup)
        partyGroupViewModel.getPartyGroup(samplePartyGroup)
        partyGroupViewModel.deletePartyGroup(samplePartyGroup)
        val updates = mapOf("name" to "CS Post Exam Hangout")
        partyGroupViewModel.updatePartyGroup(samplePartyGroup, updates)
        */

        setContent {
            FlickPicksTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BottomNavigationBar()
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    val navigationController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val selected = remember{ mutableStateOf(Icons.Default.Menu)
    }
    val currentBackStackEntry = navigationController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    val shouldShowBottomBar = currentRoute != Screens.Entry.screen && currentRoute != Screens.SignUp.screen && currentRoute != Screens.SignIn.screen
    Scaffold (
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
                            tint = if (selected.value == Icons.Default.Search) GreenJC else Color.LightGray
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
                            tint = if (selected.value == Icons.Default.Face) GreenJC else Color.LightGray
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
                            tint = if (selected.value == Icons.Default.Menu) GreenJC else Color.LightGray
                        )
                    }

                    IconButton(
                        onClick = {
                            selected.value = Icons.Default.Person
                            navigationController.navigate(Screens.Party.screen) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (selected.value == Icons.Default.Person) GreenJC else Color.LightGray
                        )
                    }

                    IconButton(
                        onClick = {
                            selected.value = Icons.Default.AccountBox
                            navigationController.navigate(Screens.Profile.screen) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.AccountBox,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (selected.value == Icons.Default.AccountBox) GreenJC else Color.LightGray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navigationController,
            startDestination = Screens.Entry.screen,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screens.Entry.screen) {
                Entry(navController = navigationController)
            }
            composable(Screens.SignUp.screen) {
                SignUp(navController = navigationController)
            }
            composable(Screens.SignIn.screen) {
                SignIn(navController = navigationController)
            }
            composable(Screens.MyFeed.screen)  {
                MyFeed(navController = navigationController)
            }
            composable(Screens.UserPreferences.screen)  {
                UserPreferences(navController = navigationController)
            }
            composable(Screens.Search.screen)  { Search() }
            composable(Screens.Friends.screen) { Friends() }
            composable(Screens.Party.screen)   {
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
            composable(Screens.PartyGroup.screen) {
                PartyGroup(navController = navigationController)
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
