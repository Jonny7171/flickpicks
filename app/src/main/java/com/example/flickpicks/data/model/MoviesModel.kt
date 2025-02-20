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
    val name: String, // Combined first and last name
    val profilePicUrl: String? = null,
    val userName: String = "",
    val password: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val friends: MutableList<String> = mutableListOf(),
    val blockedUsers: MutableList<String> = mutableListOf(),
    val incomingPendingFriends: MutableList<String> = mutableListOf(),
    val outgoingPendingFriends: MutableList<String> = mutableListOf(),
    val moviesWatched: MutableList<String> = mutableListOf(),
    val moviesRated: MutableList<String> = mutableListOf(),
    val moviesReviewed: MutableList<String> = mutableListOf(),
    val moviesLiked: MutableList<String> = mutableListOf(),
    val moviesDisliked: MutableList<String> = mutableListOf(),
    val moviesPreferences: MutableList<String> = mutableListOf()
)

data class PartyGroup(
    val id: Int,
    val name: String
)

data class Genre (
    val name: String,
    val count: Int
)
