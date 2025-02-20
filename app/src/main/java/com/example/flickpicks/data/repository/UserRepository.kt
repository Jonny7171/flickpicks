package com.example.flickpicks.data.repository

import com.example.flickpicks.data.model.UserProfile

interface UserRepository {
    fun createUser(userProfile: UserProfile)
    fun getUser(userName: String): UserProfile?
}

