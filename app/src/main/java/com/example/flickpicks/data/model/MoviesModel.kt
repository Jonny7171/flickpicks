package com.example.flickpicks.data.model

data class Movie (
    val id: String,
    val title: String,
    val release_date: String,
    val overview: String,
    val tagline: String,
    val genres: List<String>,
    val url: String
)

data class MovieReview(
    val id: Int,
    val movieTitle: String,
    val release_date: String,
    val tagline: String,
    val overview: String,
    val genres: List<String>,
    val reviewerName: String,
    val reviewText: String,
    val rating: Int,
    val streamingPlatform: String
)

data class UserProfile(
    val id: Int,
    val name: String,
    val profilePicUrl: String
)

data class PartyGroup(
    val id: Int,
    val name: String
)

data class Genre (
    val name: String,
    val count: Int
)
