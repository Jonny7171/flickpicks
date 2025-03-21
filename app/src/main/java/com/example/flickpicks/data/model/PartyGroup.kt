package com.example.flickpicks.data.model

data class PartyGroup(
    var id: Int = 0,
    var groupName: String = "",
    var members: MutableList<String> = mutableListOf(),
    var timesAvailable: MutableMap<String, Map<String, List<String>>> = mutableMapOf(),
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
) {

    constructor() : this(0, "", mutableListOf(), mutableMapOf(), Movie(
        id = "",
        title = "",
        release_date = "",
        overview = "",
        tagline = "",
        genres = listOf(),
        poster_path = "",
        vote_average = "0.0",
        trailer = null
    ), mutableListOf(), mutableListOf())
}
