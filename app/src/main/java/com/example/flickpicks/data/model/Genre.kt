package com.example.flickpicks.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Genre (
    var name: String = "",
    var count: Int = 0
)

