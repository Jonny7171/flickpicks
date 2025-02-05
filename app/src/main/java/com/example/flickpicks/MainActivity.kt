package com.example.flickpicks

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flickpicks.ui.screens.Friends
import com.example.flickpicks.ui.screens.MyFeed
import com.example.flickpicks.ui.screens.Party
import com.example.flickpicks.ui.screens.Profile
import com.example.flickpicks.ui.screens.Screens
import com.example.flickpicks.ui.screens.Search
import com.example.flickpicks.ui.theme.FlickPicksTheme
import com.example.flickpicks.ui.theme.GreenJC

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    Scaffold (
        bottomBar = {
            BottomAppBar(
                containerColor = GreenJC,
            ) {
                IconButton(
                    onClick = {
                        selected.value = Icons.Default.Search
                        navigationController.navigate(Screens.Search.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(26.dp),
                        tint = if(selected.value == Icons.Default.Search) Color.White else Color.DarkGray)
                }
                IconButton(
                    onClick = {
                        selected.value = Icons.Default.Face
                        navigationController.navigate(Screens.Friends.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(26.dp),
                        tint = if(selected.value == Icons.Default.Face) Color.White else Color.DarkGray)
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
                    Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(26.dp),
                        tint = if(selected.value == Icons.Default.Menu) Color.White else Color.DarkGray)
                }

                IconButton(
                    onClick = {
                        selected.value = Icons.Default.Person
                        navigationController.navigate(Screens.Party.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(26.dp),
                        tint = if(selected.value == Icons.Default.Person) Color.White else Color.DarkGray)
                }
                IconButton(
                    onClick = {
                        selected.value = Icons.Default.AccountBox
                        navigationController.navigate(Screens.Profile.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.AccountBox, contentDescription = null, modifier = Modifier.size(26.dp),
                        tint = if(selected.value == Icons.Default.AccountBox) Color.White else Color.DarkGray)
                }



            }
        }
    ) {
        paddingValues ->
            NavHost(navController = navigationController,
                startDestination = Screens.MyFeed.screen,
                modifier = Modifier.padding(paddingValues)) {
                composable(Screens.Search.screen) { Search() }
                composable(Screens.Friends.screen) { Friends() }
                composable(Screens.MyFeed.screen) { MyFeed()}
                composable(Screens.Party.screen) { Party() }
                composable(Screens.Profile.screen) { Profile() }

            }
    }
    
}
