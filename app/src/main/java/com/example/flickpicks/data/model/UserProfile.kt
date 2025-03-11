package com.example.flickpicks.data.model

data class UserProfile(
    var id: Int = 0,
    var name: String = "", // Combined first and last name
    var profilePicUrl: String? = null,
    var userName: String = "",
    var password: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var following: MutableList<Pair<String, String>> = mutableListOf(),
    var followers: MutableList<Pair<String, String>> = mutableListOf(),
    var blockedUsers: MutableList<String> = mutableListOf(),
    var incomingRequests: MutableList<String> = mutableListOf(),
    var outgoingRequests: MutableList<String> = mutableListOf(),
    var moviesSaved: MutableList<String> = mutableListOf(),
    var moviesReviewed: MutableList<MovieReview> = mutableListOf(),
    var moviesLiked: MutableList<String> = mutableListOf(),
    var moviesDisliked: MutableList<String> = mutableListOf(),
    var genrePreferences: MutableList<String> = mutableListOf(),
    var partyGroups: MutableList<PartyGroup> = mutableListOf()
)