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
    var chatMessages: MutableList<ChatMessage> = mutableListOf(),
    var genreMovieSuggestions: MutableList<Movie> = mutableListOf(),
    var movieVotes: MutableMap<String, VoteCounts> = mutableMapOf(),
    var gameActive: Boolean = false,
    var usersVoted: MutableMap<String, MutableList<String>> = mutableMapOf(),
    var votesSoFar: Int = 0

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
    ), mutableListOf(), mutableListOf(), mutableListOf())
}

data class VoteCounts(
    var yes: Int = 0,
    var no: Int = 0
)

