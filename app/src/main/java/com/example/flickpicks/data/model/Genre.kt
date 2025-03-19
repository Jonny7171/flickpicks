package com.example.flickpicks.data.model

import kotlinx.serialization.Serializable

val GENRE_MAP = mapOf(
    "action" to "28",
    "adventure" to "12",
    "animation" to "16",
    "comedy" to "35",
    "crime" to "80",
    "documentary" to "99",
    "drama" to "18",
    "family" to "10751",
    "fantasy" to "14",
    "history" to "36",
    "horror" to "27",
    "music" to "10402",
    "mystery" to "9648",
    "romance" to "10749",
    "science fiction" to "878",
    "tv movie" to "10770",
    "thriller" to "53",
    "war" to "10752",
    "western" to "37"
)

@Serializable
data class Genre (
    var name: String = "",
    var count: Int = 0
)

