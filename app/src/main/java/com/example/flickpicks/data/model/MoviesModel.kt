package com.example.flickpicks.data.model

data class Movies (
    val id: String,
    val title: String,
    val release_date: String,
    val overview: String,
    val tagline: String,
    val genres: List<String>,
    val url: String
)

data class Genre (
    val name: String,
    val count: Int
)