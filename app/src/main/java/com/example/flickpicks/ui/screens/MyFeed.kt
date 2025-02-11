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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flickpicks.R
import com.example.flickpicks.data.model.MovieReview

// Mock MovieReview Data
val mockReviews = listOf(
    MovieReview(1, "Inception", "2010-07-16", "Your mind is the scene of the crime.", "A mind-bending thriller.", listOf("Sci-Fi", "Thriller"), "Alice", "Amazing visuals and storytelling!", 5, "Netflix"),
    MovieReview(2, "Interstellar", "2014-11-07", "Mankind was born on Earth. It was never meant to die here.", "Exploring space and time.", listOf("Sci-Fi", "Adventure"), "Bob", "Mind-blowing space exploration!", 5, "Amazon Prime"),
    MovieReview(3, "Parasite", "2019-05-30", "Act like you own the place.", "A sharp social thriller.", listOf("Drama", "Thriller"), "Charlie", "Brilliant social commentary!", 5, "Hulu"),
    MovieReview(4, "The Dark Knight", "2008-07-18", "Why so serious?", "The greatest superhero film.", listOf("Action", "Crime"), "David", "Best Batman movie ever!", 5, "HBO Max"),
    MovieReview(5, "Dune", "2021-10-22", "Beyond fear, destiny awaits.", "A futuristic sci-fi epic.", listOf("Sci-Fi", "Adventure"), "Eve", "Stunning cinematography!", 4, "Disney+")
)

@Composable
fun MyFeed(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Trending", "Reviewed By Friends", "Recommendations")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Feed",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

        // Top Navigation Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            items(filters) { filter ->
                Button(
                    onClick = { selectedFilter = filter },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (selectedFilter == filter) Color.White else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        FeedScreen(navController, mockReviews)
    }
}

@Composable
fun FeedScreen(navController: NavController, reviews: List<MovieReview>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(reviews) { review ->
            MovieItem(review, onClick = {
                navController.navigate(Screens.MovieDetail.createRoute(review.id))
            })
        }
    }
}

@Composable
fun MovieItem(movie: MovieReview, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = movie.movieTitle, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Reviewed by: ${movie.reviewerName}", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Review: ${movie.reviewText}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Rating: ⭐ ${movie.rating}/5", style = MaterialTheme.typography.labelMedium)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Love"
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = "Like"
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_thumbs_down),
                    contentDescription = "Dislike"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { }) { Text("Save") }
                TextButton(onClick = { }) { Text("Watch Now") }
            }
        }
    }
}
