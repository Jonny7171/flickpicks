package com.example.flickpicks.data.model

data class MovieReview(
    var id: Int = 0,
    var movieId: String = "",
    var movieTitle: String = "",
    var release_date: String = "",
    var tagline: String = "",
    var overview: String = "",
    var genres: List<String> = listOf(),
    var reviewerName: String = "",
    var reviewText: String = "",
    var rating: Int = 0,
    var streamingPlatform: String = ""
)
