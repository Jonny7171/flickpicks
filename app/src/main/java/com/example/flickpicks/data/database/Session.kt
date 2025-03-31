package com.example.flickpicks.data.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserSessions")
data class Session (
    @PrimaryKey val userID: String,
    val email: String
)