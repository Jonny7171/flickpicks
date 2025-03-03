package com.example.flickpicks.data.model

data class UserProfile(
    var id: Int = 0,
    var name: String = "", // Combined first and last name
    var profilePicUrl: String? = null,
    var userName: String = "",
    var password: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var friends: MutableList<String> = mutableListOf(),
    var blockedUsers: MutableList<String> = mutableListOf(),
    var incomingPendingFriends: MutableList<String> = mutableListOf(),
    var outgoingPendingFriends: MutableList<String> = mutableListOf(),
    var moviesWatched: MutableList<String> = mutableListOf(),
    var moviesRated: MutableList<String> = mutableListOf(),
    var moviesReviewed: MutableList<String> = mutableListOf(),
    var moviesLiked: MutableList<String> = mutableListOf(),
    var moviesDisliked: MutableList<String> = mutableListOf(),
    var moviesPreferences: MutableList<String> = mutableListOf()
)