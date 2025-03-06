package com.example.flickpicks.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Movie (
    val id: String,
    val title: String,
    val release_date: String,
    val overview: String,
    val tagline: String,
    val genres: List<String>,
    val poster_path: String,
    val vote_average: String,
)

