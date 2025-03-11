package com.example.flickpicks.data.model

data class PartyGroup(
    var id: Int = 0,
    var groupName: String = "",
    var members: MutableList<UserProfile> = mutableListOf(),
    var timesAvailable: MutableMap<Pair<String, String>, Int> = mutableMapOf(),
    var winnerMovie: Movie = Movie(
        id = "",
        title = "",
        release_date = "",
        overview = "",
        tagline = "",
        genres = listOf(),
        poster_path = "",
        vote_average = "0.0",
        trailer = null
    ),
    var pastWatchedMovies: MutableList<String> = mutableListOf(),
    var chatMessages: MutableList<ChatMessage> = mutableListOf()
)
